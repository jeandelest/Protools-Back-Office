package fr.insee.protools.backend.webclient.configuration;

import lombok.Data;

@Data
public class APIProperties {

   private String url;
   private AuthProperties auth;
   private Boolean enabled=Boolean.FALSE;
   @Data
   //Infos to retrieve an oath token
   public static class AuthProperties {
      private String url;
      private String realm;
      private String clientId;
      private String clientSecret;

      @Override
      public String toString() {
         return "AuthProperties{" +
                 "url='" + url + '\'' +
                 ", realm='" + realm + '\'' +
                 ", clientId='" + clientId + '\'' +
                 '}';
      }
   }

}