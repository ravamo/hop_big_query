package org.apache.hop.workflow.actions.bigquerybulk;

import org.apache.hop.metadata.api.HopMetadata;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadata;


public class GoogleCloudStorageDetails implements IHopMetadata {
    @HopMetadataProperty
    private String keyPath;

    @HopMetadataProperty
    private String name;

    @HopMetadataProperty
    private String description;

    @HopMetadataProperty
    @Encrypted
    private String serviceAccountKey;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyPath() {
        return this.keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getType() {
        return GoogleCloudStorageFileProvider.SCHEME;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceAccountKey() {
        return this.serviceAccountKey;
    }

    public void setServiceAccountKey(String serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
    }
}
