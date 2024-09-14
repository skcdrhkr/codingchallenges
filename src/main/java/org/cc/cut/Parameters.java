package org.cc.cut;

public enum Parameters {
    FIELD_PARAM("-f"),
    DELIM_PARAM("-d");

    private String param;

    Parameters(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
