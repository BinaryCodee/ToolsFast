package it.bypasser.toolsfast.api;

public class ToolsFastAPIHolder {
    public static ToolsFastAPI INSTANCE;
    public static void set(ToolsFastAPI impl) { INSTANCE = impl; }
}
