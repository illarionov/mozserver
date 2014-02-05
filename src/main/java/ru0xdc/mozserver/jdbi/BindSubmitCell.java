package ru0xdc.mozserver.jdbi;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import ru0xdc.mozserver.model.SubmitCell;

import java.lang.annotation.*;

@BindingAnnotation(BindSubmitCell.CellBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindSubmitCell {

    public static class CellBinderFactory implements BinderFactory
    {
        public Binder build(Annotation annotation)
        {
            return new Binder<BindSubmitCell, SubmitCell>()
            {
                public void bind(SQLStatement q, BindSubmitCell bind, SubmitCell submitCell)
                {
                    q.bind("mcc", submitCell.getMcc() != SubmitCell.UNKNOWN_IDENT ? submitCell.getMcc() : 0);
                    q.bind("mnc", submitCell.getMnc() != SubmitCell.UNKNOWN_IDENT ? submitCell.getMnc() : 0);
                    q.bind("lac", submitCell.getLac());
                    q.bind("cid", submitCell.getCid());
                    q.bind("psc", submitCell.getPsc());
                    q.bind("radio", submitCell.getRadio());
                }
            };
        }
    }
}
