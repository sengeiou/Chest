package com.stur.gms.gts;

public class BusinessLogic
{
    /*public static final String DEVICE_FILE = "/sdcard/bl";
    private AuthenticationStatusEnum mAuthenticationStatus;
    public boolean mConditionalTestsEnabled;
    protected Map<String, List<BusinessLogicRulesList>> mRules;
    protected Date mTimestamp;

    public BusinessLogic() {
        this.mAuthenticationStatus = AuthenticationStatusEnum.UNKNOWN;
    }

    private void applyLogicsFor(final String s, final BusinessLogicExecutor businessLogicExecutor) {
        final HashMap<String, RuntimeException> hashMap = new HashMap<String, RuntimeException>();
        final HashMap<String, RuntimeException> hashMap2 = new HashMap<String, RuntimeException>();
        final List<BusinessLogicRulesList> list = this.mRules.get(s);
        for (int i = 0; i < list.size(); ++i) {
            final BusinessLogicRulesList list2 = list.get(i);
            final String cleanDescription = this.cleanDescription(list2.getDescription(), i);
            try {
                list2.invokeRules(businessLogicExecutor);
            }
            catch (RuntimeException ex) {
                if (AssumptionViolatedException.class.isInstance(ex)) {
                    hashMap2.put(cleanDescription, ex);
                    businessLogicExecutor.logInfo("Test %s (%s) skipped for reason: %s", s, cleanDescription, ex.getMessage());
                }
                else {
                    hashMap.put(cleanDescription, ex);
                }
            }
        }
        if (hashMap2.size() == list.size()) {
            throwAggregatedException(hashMap2, false);
            return;
        }
        if (hashMap.size() > 0) {
            throwAggregatedException(hashMap, true);
        }
    }

    private String cleanDescription(final String s, final int n) {
        if (s != null && s.length() != 0) {
            return s;
        }
        return Integer.toString(n);
    }

    private boolean hasLogicsFor(final String s) {
        final List<BusinessLogicRulesList> list = this.mRules.get(s);
        return list != null && list.size() > 1;
    }

    private static void throwAggregatedException(final Map<String, RuntimeException> map, final boolean b) {
        final Set<String> keySet = map.keySet();
        final String[] array = keySet.toArray(new String[keySet.size()]);
        final StringBuilder sb = new StringBuilder("");
        final Object[] array2 = { null };
        String s;
        if (b) {
            s = "failed";
        }
        else {
            s = "skipped";
        }
        array2[0] = s;
        sb.append(String.format("Test %s for cases: ", array2));
        sb.append(String.join(", ", (CharSequence[])array));
        sb.append("\nReasons include:");
        for (final String s2 : array) {
            final RuntimeException ex = map.get(s2);
            sb.append(String.format("\nMessage [%s]: %s", s2, ex.getMessage()));
            if (b) {
                final StringWriter stringWriter = new StringWriter();
                ex.printStackTrace(new PrintWriter(stringWriter));
                sb.append(String.format("\nStack Trace: %s", stringWriter.toString()));
            }
        }
        if (b) {
            throw new RuntimeException(sb.toString());
        }
        throw new AssumptionViolatedException(sb.toString());
    }

    public void applyLogicFor(final String s, final BusinessLogicExecutor businessLogicExecutor) {
        if (!this.hasLogicFor(s)) {
            return;
        }
        if (this.hasLogicsFor(s)) {
            this.applyLogicsFor(s, businessLogicExecutor);
            return;
        }
        this.mRules.get(s).get(0).invokeRules(businessLogicExecutor);
    }

    public String getAuthenticationStatusMessage() {
        switch (this.mAuthenticationStatus) {
            default: {
                return "something went wrong, please try again.";
            }
            case NOT_AUTHORIZED: {
                return "service account is not authorized to access information for this device. Please verify device properties are set correctly and account permissions are configured to the Business Logic Api.";
            }
            case NOT_AUTHENTICATED: {
                return "authorization failed, please ensure the service account key is properly installed.";
            }
            case AUTHORIZED: {
                return "Authorized";
            }
        }
    }

    public Date getTimestamp() {
        return this.mTimestamp;
    }

    public boolean hasLogicFor(final String s) {
        final List<BusinessLogicRulesList> list = this.mRules.get(s);
        return list != null && !list.isEmpty();
    }

    public boolean isAuthorized() {
        return AuthenticationStatusEnum.AUTHORIZED.equals(this.mAuthenticationStatus);
    }

    public void setAuthenticationStatus(final String s) {
        try {
            this.mAuthenticationStatus = Enum.valueOf(AuthenticationStatusEnum.class, s);
        }
        catch (IllegalArgumentException ex) {
            this.mAuthenticationStatus = AuthenticationStatusEnum.UNKNOWN;
        }
    }

    protected enum AuthenticationStatusEnum
    {
        AUTHORIZED,
        NOT_AUTHENTICATED,
        NOT_AUTHORIZED,
        UNKNOWN;
    }

    protected static class BusinessLogicRule
    {
        protected List<BusinessLogicRuleAction> mActions;
        protected List<BusinessLogicRuleCondition> mConditions;

        public BusinessLogicRule(final List<BusinessLogicRuleCondition> mConditions, final List<BusinessLogicRuleAction> mActions) {
            this.mConditions = mConditions;
            this.mActions = mActions;
        }

        public void invokeActions(final BusinessLogicExecutor businessLogicExecutor) {
            final Iterator<BusinessLogicRuleAction> iterator = this.mActions.iterator();
            while (iterator.hasNext()) {
                iterator.next().invoke(businessLogicExecutor);
            }
        }

        public boolean invokeConditions(final BusinessLogicExecutor businessLogicExecutor) {
            final Iterator<BusinessLogicRuleCondition> iterator = this.mConditions.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().invoke(businessLogicExecutor)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected static class BusinessLogicRuleAction
    {
        protected List<String> mMethodArgs;
        protected String mMethodName;

        public BusinessLogicRuleAction(final String mMethodName, final List<String> mMethodArgs) {
            this.mMethodName = mMethodName;
            this.mMethodArgs = mMethodArgs;
        }

        public void invoke(final BusinessLogicExecutor businessLogicExecutor) {
            businessLogicExecutor.executeAction(this.mMethodName, (String[])this.mMethodArgs.toArray(new String[this.mMethodArgs.size()]));
        }
    }

    protected static class BusinessLogicRuleCondition
    {
        protected List<String> mMethodArgs;
        protected String mMethodName;
        protected boolean mNegated;

        public BusinessLogicRuleCondition(final String mMethodName, final List<String> mMethodArgs, final boolean mNegated) {
            this.mMethodName = mMethodName;
            this.mMethodArgs = mMethodArgs;
            this.mNegated = mNegated;
        }

        public boolean invoke(final BusinessLogicExecutor businessLogicExecutor) {
            return this.mNegated != businessLogicExecutor.executeCondition(this.mMethodName, (String[])this.mMethodArgs.toArray(new String[this.mMethodArgs.size()]));
        }
    }

    protected static class BusinessLogicRulesList
    {
        protected String mDescription;
        protected List<BusinessLogicRule> mRulesList;

        public BusinessLogicRulesList(final List<BusinessLogicRule> mRulesList) {
            this.mRulesList = mRulesList;
        }

        public BusinessLogicRulesList(final List<BusinessLogicRule> mRulesList, final String mDescription) {
            this.mRulesList = mRulesList;
            this.mDescription = mDescription;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public List<BusinessLogicRule> getRules() {
            return this.mRulesList;
        }

        public void invokeRules(final BusinessLogicExecutor businessLogicExecutor) {
            for (final BusinessLogicRule businessLogicRule : this.mRulesList) {
                if (businessLogicRule.invokeConditions(businessLogicExecutor)) {
                    businessLogicRule.invokeActions(businessLogicExecutor);
                }
            }
        }
    }*/
}
