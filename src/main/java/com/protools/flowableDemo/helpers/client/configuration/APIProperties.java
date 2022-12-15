package com.protools.flowableDemo.helpers.client.configuration;

import lombok.Data;

@Data
public class APIProperties {

   private String url;
   private String realm;
   private Boolean enabled=Boolean.FALSE;

}