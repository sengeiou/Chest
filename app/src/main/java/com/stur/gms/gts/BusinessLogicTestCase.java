package com.stur.gms.gts;

public class BusinessLogicTestCase
{
    /*private static final String PARAM_START = "[";
    protected BusinessLogic mBusinessLogic;
    protected boolean mCanReadBusinessLogic;
    public TestName mTestCase;

    public BusinessLogicTestCase() {
        this.mTestCase = new TestName();
        this.mCanReadBusinessLogic = true;
    }

    public static void failTest(final String s) {
        Assert.fail(s);
    }

    protected static Context getContext() {
        return getInstrumentation().getTargetContext();
    }

    protected static Instrumentation getInstrumentation() {
        return InstrumentationRegistry.getInstrumentation();
    }

    public static void skipTest(final String s) {
        Assume.assumeTrue(s, false);
    }

    protected void executeBusinessLogic() {
        String s = this.mTestCase.getMethodName();
        Assert.assertTrue(String.format("Test \"%s\" is unable to execute as it depends on the missing remote configuration.", s), this.mCanReadBusinessLogic);
        if (s.contains("[")) {
            s = s.substring(0, s.lastIndexOf("["));
        }
        final String format = String.format("%s#%s", this.getClass().getName(), s);
        if (this.mBusinessLogic.hasLogicFor(format)) {
            Log.i("Finding business logic for test case: ", format);
            this.mBusinessLogic.applyLogicFor(format, new BusinessLogicDeviceExecutor(getContext(), this));
        }
    }

    @Before
    public void handleBusinessLogic() {
        this.loadBusinessLogic();
        this.executeBusinessLogic();
    }

    protected void loadBusinessLogic() {
        final File file = new File("/sdcard/bl");
        if (file.canRead()) {
            this.mBusinessLogic = BusinessLogicFactory.createFromFile(file);
            return;
        }
        this.mCanReadBusinessLogic = false;
    }

    public void mapPut(final String s, final String s2, final String s3) {
        final Field[] declaredFields = this.getClass().getDeclaredFields();
        final int length = declaredFields.length;
        boolean b = false;
        for (final Field field : declaredFields) {
            if (field.getName().equalsIgnoreCase(s) && Map.class.isAssignableFrom(field.getType())) {
                try {
                    ((Map)field.get(this)).put(s2, s3);
                    b = true;
                }
                catch (IllegalAccessException ex) {
                    Log.w(String.format("failed to invoke mapPut on field \"%s\". Resuming...", field.getName()), (Throwable)ex);
                }
            }
        }
        if (b) {
            return;
        }
        throw new RuntimeException(String.format("Failed to find map %s in class %s", s, this.getClass().getName()));
    }*/
}
