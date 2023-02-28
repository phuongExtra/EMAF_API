package com.emaf.service.enumeration;

/**
 * ETable
 *
 * @author khale
 * @since 2021/11/04
 */
public enum ETable {

    FS_USER("fs_user", "user_id"),
    FS_GROUP("fs_group", "group_id");

    private String table;
    private String key;

    ETable(final String table, final String key) {
        this.table = table;
        this.key = key;
    }

    public final String TABLE() {
        return this.table;
    }

    public final String KEY() {
        return this.key;
    }

}
