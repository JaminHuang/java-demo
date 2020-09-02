package com.demo.ibatx.generator.template;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class TableDetail implements Serializable {

    private static final long serialVersionUID = -5253202303789722701L;

    public static final String TABLE_NAME = "table";

    public static final String ID_NAME = "id";

    public static final String VERSION_NAME = "version";

    public static final String LOGIC_DELETE = "logicDelete";

    public static final String DOMAIN_NAME = "domainObjectName";

    /**
     * 默认分页
     */
    public static final String PAGEABLE = "pageable";

    public static final String SCHEMA = "schema";

    private String tableName;

    private String idName;

    private String versionName;

    private String logicDeleteName;

    private String pageable;

    private String domainObjectName;

    private String schema;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableDetail that = (TableDetail) o;
        return Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
