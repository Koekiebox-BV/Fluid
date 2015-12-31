/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluid.ws.client.v1;

/**
 * Created by jasonbruwer on 2015/12/29.
 */
public abstract class ABaseFieldClient extends ABaseClientWS{

    /**
     *
     */
    public static final class FieldMetaData
    {
        /**
         *
         */
        public static final class Text
        {
            public static final String PLAIN = "Plain";
            public static final String MASKED = "Masked";
            public static final String BARCODE = "Barcode";
            public static final String LATITUDE_AND_LONGITUDE = "Latitude and Longitude";
        }

        /**
         *
         */
        public static final class TrueFalse
        {
            public static final String TRUE_FALSE = "True False";
        }

        /**
         *
         */
        public static final class ParagraphText
        {
            public static final String PLAIN = "Plain";
            public static final String HTML = "HTML";
        }

        /**
         *
         */
        public static final class MultiChoice
        {
            public static final String PLAIN = "Plain";
            public static final String PLAIN_SEARCH = "Plain with Search";

            public static final String SELECT_MANY = "Select Many";
            public static final String SELECT_MANY_SEARCH = "Select Many with Search";
        }

        /**
         *
         */
        public static final class DateTime
        {
            public static final String DATE = "Date";
            public static final String DATE_AND_TIME = "Date and Time";
        }

        /**
         *
         */
        public static final class Decimal
        {
            public static final String PLAIN = "Plain";
            public static final String RATING = "Rating";
            public static final String SPINNER = "Spinner";
            public static final String SLIDER = "Slider";

            //Keywords...
            public static final String UNDERSCORE = "_";
            public static final String SQ_OPEN = "[";
            public static final String SQ_CLOSE = "]";

            public static final String MIN = "Min";
            public static final String MAX = "Max";
            public static final String STEP_FACTOR = "StepFactor";
            public static final String PREFIX = "Prefix";
        }

        /**
         *
         */
        public static final class TableField {

            public static final String SUM_DECIMALS = "SumDecimals";

            public static final String UNDERSCORE = "_";
            public static final String SQ_OPEN = "[";
            public static final String SQ_CLOSE = "]";
        }
    }

    /**
     *
     * @return
     */
    protected String getMetaDataForDecimalAs(
            String metaDataPrefixParam,
            double minParam,
            double maxParam,
            double stepFactorParam,
            String prefixParam)
    {
        StringBuffer returnBuffer = new StringBuffer();

        if(metaDataPrefixParam != null && !metaDataPrefixParam.isEmpty())
        {
            returnBuffer.append(metaDataPrefixParam);
        }

        //Min...
        returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);
        returnBuffer.append(FieldMetaData.Decimal.MIN);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(minParam);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
        returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

        //Max...
        returnBuffer.append(FieldMetaData.Decimal.MAX);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(maxParam);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
        returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

        //Step Factor...
        returnBuffer.append(FieldMetaData.Decimal.STEP_FACTOR);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(stepFactorParam);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);
        returnBuffer.append(FieldMetaData.Decimal.UNDERSCORE);

        //Prefix
        String prefix = (prefixParam == null) ? "": prefixParam;

        returnBuffer.append(FieldMetaData.Decimal.PREFIX);
        returnBuffer.append(FieldMetaData.Decimal.SQ_OPEN);
        returnBuffer.append(prefix);
        returnBuffer.append(FieldMetaData.Decimal.SQ_CLOSE);

        return returnBuffer.toString();
    }
}
