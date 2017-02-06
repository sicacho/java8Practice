package com.company.googledrive;

import org.apache.http.client.protocol.HttpClientContext;

/**
 * Created by Administrator on 1/21/2017.
 */
public interface MigrateService {
  public void migrate(String urlG, String newname) throws Exception;
}
