package com.b4edriver.Citrus;

//import com.citrus.sdk.Environment;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by vijay on 14/7/16.
 */
public enum PaytmEnvironment {



    LIVE {
        @Override
        public String getMID() {
            return "NbaTec92533507910524";
        }

        @Override
        public String getMerchantKey() {
            return "rkY_z4WAbxmvIR%#";
        }  //rkY_z4WAbxmvIR%#

        @Override
        public String getWebsite() {
            return "NbaTecWAP";
        }

        @Override
        public String getChannelId() {
            return "WAP";
        }

        @Override
        public String getIndustryTypeId() {
            return "Retail109";
        }

        @Override
        public String getClientID() {
            return "merchant-nba-tech-solutions";
        }

        @Override
        public String getSecretKey() {
            return "17803a89-7cac-4edb-be91-8445a643e22c";
        }

        @Override
        public String getAuth() {
            byte[]   bytesEncoded = Base64.encodeBase64((getClientID()+":"+getSecretKey()).getBytes());
            String auth = "Basic " + new String(bytesEncoded );
            System.out.println("ecncoded value is " +auth);
            return auth;
        }

        @Override
        public String getWalletUrl() {
            return "https://secure.paytm.in/";
        }

        @Override
        public String getAccountUrl() {
            return "https://accounts.paytm.com/";
        }

        @Override
        public String getCallBackUrl() {
            return "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
        }

        @Override
        public boolean isLive() {
            return true;
        }
    },
    DEMO {
        @Override
        public String getMID() {
            return "NBATec45564621192191";
        } //NBATec45564621192191 Mksing29513853262834

        @Override
        public String getMerchantKey() {
            return "CKtfB5y403YiPcw1";
        }  //CKtfB5y403YiPcw1 X7Bo&TTQ4UbF&#jS

        @Override
        public String getWebsite() {
            return "APPSTAGING";
        } //WEBSTAGING //APP_STAGING

        @Override
        public String getChannelId() {
            return "WAP";
        }

        @Override
        public String getIndustryTypeId() {
            return "Retail";
        }

        @Override
        public String getClientID() {
            return "merchant-nba-tech-staging";
        }

        @Override
        public String getSecretKey() {
            return "";
        }

        @Override
        public String getAuth() {
            return "Basic bWVyY2hhbnQtbmJhLXRlY2gtc3RhZ2luZzoxMjUwZTg4NS0xNGY1LTQyODAtOWViYS02YjhkZWE4ZGQ3ZTk=";
        }

        @Override
        public String getWalletUrl() {
            return "https://pguat.paytm.com/";
        }

        @Override
        public String getAccountUrl() {
            return "https://accounts-uat.paytm.com/signin/";
        }

        @Override
        public String getCallBackUrl() {
            return "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
        }

        @Override
        public boolean isLive() {
            return false;
        }
    };







    public abstract String getMID();
    public abstract String getMerchantKey();
    public abstract String getWebsite();
    public abstract String getChannelId();
    public abstract String getIndustryTypeId();
    public abstract String getClientID();
    public abstract String getSecretKey();
    public abstract String getAuth();
    public abstract String getWalletUrl();
    public abstract String getAccountUrl();
    public abstract String getCallBackUrl();
    public abstract boolean isLive();
}
