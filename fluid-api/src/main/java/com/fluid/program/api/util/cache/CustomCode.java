package com.fluid.program.api.util.cache;

/**
 * User: jasonbruwer Date: 2014/07/11 Time: 3:14 PM
 */
public class CustomCode {
    public static final String JAVA_LANG_OBJECT = "java.lang.Object";


    /**
     *
     */
    public static final class IWord {
        public static final String NAME = "com.flowjob.domain.ruleengine.IWord";

        public static final String METHOD_getWord = "getWord";
    }

    /**
     *
     */
    public static final class ADataType {
        public static final String NAME = "com.flowjob.domain.ruleengine.datatype.ADataType";

        public static final String VARIABLE_fieldValueForCachingId = "fieldValueForCachingId";
        public static final String METHOD_getValue = "getValue";
    }

    /**
     *
     */
    public static final class MultipleChoice {
        public static final String NAME = "com.flowjob.domain.ruleengine.datatype.MultipleChoice";

        public static final String METHOD_getAvailableChoices = "getAvailableChoices";
        public static final String METHOD_getSelectedChoices = "getSelectedChoices";

    }

    /**
     *
     */
    public static final class MultipleChoiceValue {
        public static final String NAME = "com.flowjob.domain.ruleengine.datatype.MultipleChoiceValue";
    }


}
