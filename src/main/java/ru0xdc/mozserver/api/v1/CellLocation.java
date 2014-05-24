package ru0xdc.mozserver.api.v1;

import com.google.common.base.Optional;
import com.palominolabs.jersey.cors.Cors;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.yammer.metrics.annotation.Timed;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru0xdc.mozserver.MozserverConfiguration;
import ru0xdc.mozserver.model.location.CellLocationMozillaRequest;
import ru0xdc.mozserver.model.location.CellLocationYandexResponse;
import ru0xdc.mozserver.model.location.MozillaResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/v1/cell/location")
@Produces(MediaType.APPLICATION_JSON)
public class CellLocation {

	Logger mLog = LoggerFactory.getLogger(SubmitResource.class.getName());

	private MozserverConfiguration mConfiguration;

	public CellLocation(MozserverConfiguration configuration) {
		mConfiguration = configuration;
	}

	@GET
	@Path("/yandex")
	@Timed
	@Cors
	public ru0xdc.mozserver.model.location.CellLocation getYandex(
			@QueryParam("mcc") Integer pMcc,
			@QueryParam("mnc") Integer pMnc,
			@QueryParam("lac") Integer pLac,
			@QueryParam("cid") Integer pCid) {

		if (pMcc == null || pMnc == null || pLac == null || pCid == null) {
			throw new WebApplicationException(Response.status(400).entity(new ru0xdc.mozserver.model.location.CellLocation.Error("Invalid argument")).build());
		}

		Client client = Client.create();
		WebResource r = client.resource("http://mobile.maps.yandex.net/cellid_location/")
				.queryParam("countrycode", String.valueOf(pMcc))
				.queryParam("operatorid", String.valueOf(pMnc))
				.queryParam("lac", String.valueOf(pLac))
				.queryParam("cellid", String.valueOf(pCid));

		CellLocationYandexResponse response;
		try {
			response = r.accept(MediaType.APPLICATION_XML_TYPE)
					.get(CellLocationYandexResponse.class);
		} catch (UniformInterfaceException uie) {
			throw new WebApplicationException(
					Response.status(uie.getResponse().getStatus()).entity(
							new ru0xdc.mozserver.model.location.CellLocation.Error(uie.getResponse().toString())).build());
		}

		return new ru0xdc.mozserver.model.location.CellLocation(response.coordinates.latitude, response.coordinates.longitude);
	}

	@GET
	@Path("/mozilla")
	@Timed
	@Cors
	public ru0xdc.mozserver.model.location.CellLocation getMozilla(
			@QueryParam("mcc") Integer pMcc,
			@QueryParam("mnc") Integer pMnc,
			@QueryParam("lac") Integer pLac,
			@QueryParam("cid") Integer pCid,
			@QueryParam("network_radio") Optional<String> pRadio,
			@QueryParam("psc") Optional<Integer> pPsc) {
		if (pMcc == null || pMnc == null || pLac == null || pCid == null) {
			throw new WebApplicationException(Response.status(400).entity(new ru0xdc.mozserver.model.location.CellLocation.Error("Invalid argument")).build());
		}

		Client client = Client.create();
		WebResource r = client.resource("https://location.services.mozilla.com/v1/search")
				.queryParam("key", mConfiguration.getMozLocServicesKey());

		MozillaResponse response;
		try {
			CellLocationMozillaRequest req = new CellLocationMozillaRequest(pMcc, pMnc, pLac, pCid,
					pPsc.orNull(), pRadio.orNull());

			response = r.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_JSON)
					.post(MozillaResponse.class, req);
		} catch (UniformInterfaceException uie) {
			throw new WebApplicationException(
					Response.status(uie.getResponse().getStatus()).entity(
							new ru0xdc.mozserver.model.location.CellLocation.Error(uie.getResponse().toString())).build());
		}
		return new ru0xdc.mozserver.model.location.CellLocation(response.lat, response.lon, (float) response.accuracy);
	}

	@GET
	@Path("/google")
	@Timed
	@Cors
	public ru0xdc.mozserver.model.location.CellLocation getGoogle(
			@QueryParam("mcc") Integer pMcc,
			@QueryParam("mnc") Integer pMnc,
			@QueryParam("lac") Integer pLac,
			@QueryParam("cid") Integer pCid) {

		if (pMcc == null || pMnc == null || pLac == null || pCid == null) {
			throw new WebApplicationException(Response.status(400).entity(new ru0xdc.mozserver.model.location.CellLocation.Error("Invalid argument")).build());
		}

		HttpURLConnection connection = null;
		DataOutputStream os = null;
		try {
			URL url = new URL("http://www.google.com/glm/mmap");
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setReadTimeout(25 * 1000);
			connection.connect();
			os = new DataOutputStream(connection.getOutputStream());
			os.writeShort(0x0e);
			os.writeLong(0);
			os.writeInt(0);
			os.writeShort(0);
			os.writeByte(27);
			os.writeInt(0); os.writeInt(0); os.writeInt(3);
			os.writeUTF("");
			os.writeInt(pCid);
			os.writeInt(pLac);
			os.writeInt(pMnc);
			os.writeInt(pMcc);
			os.writeInt(0xffffffff);
			os.writeInt(0);
			os.flush();

			DataInputStream dis = new DataInputStream(connection.getInputStream());
			dis.readShort();
			dis.readByte();
			int errorCode = dis.readInt();
			if (errorCode == 0) {
				int latE6 = dis.readInt();
				int lonE6 = dis.readInt();
				return new ru0xdc.mozserver.model.location.CellLocation(latE6 / 1.0e6, lonE6 / 1.0e6);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.status(400).entity(
					new ru0xdc.mozserver.model.location.CellLocation.Error("ioe" + e.getLocalizedMessage())
			).build());
		} finally {
			 if (connection != null) {
				 connection.disconnect();
			 }
		}
		throw new WebApplicationException(Response.status(404).entity(
				new ru0xdc.mozserver.model.location.CellLocation.Error("Location not found")).build());
	}

}