package org.apache.hop.workflow.actions.bigquerybulk;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hop.core.Const;
import org.apache.hop.core.Result;
import org.apache.hop.core.annotations.Action;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopXmlException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.core.xml.XmlHandler;
import org.apache.hop.core.vfs.HopVfs;
import org.apache.hop.workflow.action.ActionBase;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.w3c.dom.Node;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Action(id = "GoogleBigQueryStorageLoad",
         name = "GoogleBigQueryStorageLoad.Name",
         description = "GoogleBigQueryStorageLoad.TooltipDesc",
         image = "google-bigquery.svg",
         categoryDescription = "i18n:org.apache.hop.job:JobCategory.Category.BulkLoading",
         i18nPackageName = "org.itfactory.kettle.job.entries.bigqueryloader",
         documentationUrl = "Products/Google_BigQuery_Loader",
         casesUrl = "GoogleBigQueryStorageLoad.CasesURL", forumUrl = "GoogleBigQueryStorageLoad.ForumURL")

public class JobEntryBigQueryLoader extends ActionBase implements Cloneable, IAction {
    public static final String CONNECTION = "connection";

    private static Class<?> PKG = JobEntryBigQueryLoader.class;

    public static final String TYPE_JSON = "JSON";

    public static final String TYPE_CSV = "CSV";

    public static final String TYPE_AVRO = "Avro";

    private String datasetName = null;

    private String tableName = null;

    private String sourceUri = null;

    private String fileType = "CSV";

    private String delimiter = ",";

    private String quote = "";

    private String leadingRowsToSkip = "1";

    private String connection = "";

    private boolean truncateTable = false;

    private boolean isEnvironmentAuth = false;

    private Map<String, String> tableFields;

    private String[] fieldNames = new String[0];

    private String[] fieldTypes = new String[0];

    BigQuery bigQuery;

    public JobEntryBigQueryLoader() {
        super("BigQueryLoader", "Loads Google Cloud Storage files in to Google BigQuery");
        this.tableFields = new HashMap<>();
    }

    public JobEntryBigQueryLoader(String name) {
        super(name, "Loads Google Cloud Storage files in to Google BigQuery");
        this.tableFields = new HashMap<>();
    }

    public Object clone() {
        JobEntryBigQueryLoader je = (JobEntryBigQueryLoader) super.clone();
        return je;
    }

    /*public String getDialogClassName() {
        return "com.pentaho.di.ui.job.entries.google.bigquery.JobEntryBigQueryLoaderDialog";
    }*/

    public boolean evaluates() {
        return true;
    }

    public String getXml() {
        StringBuilder retval = new StringBuilder(650);
        retval.append( super.getXml() );
        retval.append("      ").append(XmlHandler.addTagValue("datasetName", this.datasetName));
        retval.append("      ").append(XmlHandler.addTagValue("tableName", this.tableName));
        retval.append("      ").append(XmlHandler.addTagValue("sourceUri", this.sourceUri));
        retval.append("      ").append(XmlHandler.addTagValue("delimiter", this.delimiter));
        retval.append("      ").append(XmlHandler.addTagValue("quote", this.quote));
        retval.append("      ").append(XmlHandler.addTagValue("fileType", this.fileType));
        retval.append("      ").append(XmlHandler.addTagValue("leadingRowsToSkip", this.leadingRowsToSkip));
        retval.append("      ").append(XmlHandler.addTagValue("connection", this.connection));
        retval.append("      ").append(XmlHandler.addTagValue("truncateTable", this.truncateTable));
        retval.append("      ").append(XmlHandler.addTagValue("isEnvironmentAuth", this.isEnvironmentAuth));
        retval.append("    <fields>").append(Const.CR);
        for (int i = 0; i < this.fieldNames.length; i++) {
            retval.append("      <field>").append(Const.CR);
            retval.append("        ").append(XmlHandler.addTagValue("name", this.fieldNames[i]));
            retval.append("        ").append(XmlHandler.addTagValue("type", this.fieldTypes[i]));
            retval.append("      </field>").append(Const.CR);
        }
        retval.append("    </fields>").append(Const.CR);
        return retval.toString();
    }

    @Override
    public void loadXml(Node entrynode, IHopMetadataProvider metadataProvider) throws HopXmlException {
        try {
            super.loadXml( entrynode );
            this.datasetName = XmlHandler.getTagValue(entrynode, "datasetName");
            this.tableName = XmlHandler.getTagValue(entrynode, "tableName");
            this.sourceUri = XmlHandler.getTagValue(entrynode, "sourceUri");
            String d = XmlHandler.getTagValue(entrynode, "delimiter");
            if (null == d) {
                this.delimiter = ",";
            } else {
                this.delimiter = d;
            }
            String q = XmlHandler.getTagValue(entrynode, "quote");
            if (null == q) {
                this.quote = "";
            } else {
                this.quote = q;
            }
            this.fileType = XmlHandler.getTagValue(entrynode, "fileType");
            this.leadingRowsToSkip = XmlHandler.getTagValue(entrynode, "leadingRowsToSkip");
            this.connection = XmlHandler.getTagValue(entrynode, "connection");
            this.truncateTable = "Y".equalsIgnoreCase(XmlHandler.getTagValue(entrynode, "truncateTable"));
            this.isEnvironmentAuth = "Y".equalsIgnoreCase(XmlHandler.getTagValue(entrynode, "isEnvironmentAuth"));
            Node fields = XmlHandler.getSubNode(entrynode, "fields");
            int nrfields = XmlHandler.countNodes(fields, "field");
            allocate(nrfields);
            for (int i = 0; i < nrfields; i++) {
                Node fnode = XmlHandler.getSubNodeByNr(fields, "field", i);
                this.fieldNames[i] = XmlHandler.getTagValue(fnode, "name");
                this.fieldTypes[i] = XmlHandler.getTagValue(fnode, "type");
            }
        } catch ( HopException e ) {
            throw new HopXmlException( "Unable to load action of type 'MSsql bulk load' from XML node", e );
        }
    }

    private IMetaStore findMetaStore() {
        if (getMetaStore() != null) {
            return getMetaStore();
        }
        if (getParentJobMeta() != null && getParentJobMeta().getMetaStore() != null)
            return getParentJobMeta().getMetaStore();
        if (getParentJobMeta() != null && getParentJobMeta().getMetastoreLocatorOsgi() != null)
            return getParentJobMeta().getMetastoreLocatorOsgi().getMetastore();
        return null;
    }

    public BigQuery getBigQuery(String connection, boolean isEnvironmentAuth) {
        if (isEnvironmentAuth)
            try {
                return (BigQuery) BigQueryOptions.getDefaultInstance().getService();
            } catch (Exception e) {
                return null;
            }
        if (!Utils.isEmpty(connection)) {
            GoogleCloudStorageDetails googleCloudStorageDetails;
            IMetaStore metaStore = findMetaStore();
            if (metaStore != null) {
                googleCloudStorageDetails = (GoogleCloudStorageDetails) ConnectionManager.getInstance().getConnectionDetails(metaStore, GoogleCloudStorageFileProvider.SCHEME, connection);
            } else {
                googleCloudStorageDetails = (GoogleCloudStorageDetails) ConnectionManager.getInstance().getConnectionDetails(GoogleCloudStorageFileProvider.SCHEME, connection);
            }
            if (googleCloudStorageDetails != null) {
                String credentialsPath = googleCloudStorageDetails.getKeyPath();
                if (!Utils.isEmpty(credentialsPath)) {
                    this.log.logBasic("Using credentials file " + credentialsPath + " for Google Cloud Storage");
                    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
                        ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
                        return (BigQuery) ((BigQueryOptions.Builder) BigQueryOptions.newBuilder()
                                .setCredentials((Credentials) serviceAccountCredentials))
                                .build()
                                .getService();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public Result execute(Result previousResult, int nr) throws HopXmlException {
        FormatOptions formatOptions;
        String subConnection = environmentSubstitute(this.connection);
        this.bigQuery = getBigQuery(subConnection, this.isEnvironmentAuth);
        Result result = previousResult;
        result.setResult(false);
        result.setNrErrors(0L);
        DatasetInfo datasetInfo = Dataset.of(environmentSubstitute(this.datasetName));
        Dataset dataset = this.bigQuery.getDataset(datasetInfo.getDatasetId(), new BigQuery.DatasetOption[0]);
        if (dataset == null)
            dataset = this.bigQuery.create(datasetInfo, new BigQuery.DatasetOption[0]);
        TableId tableId = TableId.of(dataset.getDatasetId().getDataset(), environmentSubstitute(this.tableName));
        Table table = this.bigQuery.getTable(tableId, new BigQuery.TableOption[0]);
        if (table == null) {
            logDebug("Table not found: " + environmentSubstitute(this.tableName));
            logDebug("Creating table based on mapping specification");
            List<Field> fieldList = new ArrayList<>();
            int nrfields = (getFieldNames()).length;
            for (int i = 0; i < nrfields; i++)
                fieldList.add(Field.of(this.fieldNames[i], LegacySQLTypeName.valueOf(this.fieldTypes[i]), new Field[0]));
            Schema schema = Schema.of(fieldList);
            table = this.bigQuery.create(TableInfo.of(tableId, (TableDefinition) StandardTableDefinition.of(schema)), new BigQuery.TableOption[0]);
        }
        List<String> fileList = new ArrayList<>();
        String fileTypeString = environmentSubstitute(this.fileType);
        Variables variables = new Variables();
        if (subConnection != null)
            variables.setVariable("connection", environmentSubstitute(this.connection));
        ConnectionManager.getInstance().setMetastoreSupplier(this::findMetaStore);
        FileObject fileObject = HopVfs.getFileObject(environmentSubstitute(this.sourceUri), (VariableSpace) variables);
        boolean isFolder = false;
        try {
            isFolder = fileObject.isFolder();
            if (isFolder) {
                FileObject[] fileObjects = fileObject.getChildren();
                for (FileObject fileObject1 : fileObjects) {
                    if (fileObject1.getName().getExtension().equals(fileTypeString.toLowerCase()))
                        fileList.add(fileObject1.getPublicURIString());
                }
            }
        } catch (FileSystemException fileSystemException) {
        }
        if (!isFolder)
            fileList.add(environmentSubstitute(this.sourceUri));
        if ("CSV".equals(fileTypeString)) {
            CsvOptions csvOptions = CsvOptions.newBuilder().setFieldDelimiter(environmentSubstitute(this.delimiter)).setQuote(environmentSubstitute(this.quote)).setSkipLeadingRows(Integer.valueOf(environmentSubstitute(this.leadingRowsToSkip)).intValue()).build();
        } else if ("Avro".equals(fileTypeString)) {
            formatOptions = FormatOptions.avro();
        } else {
            formatOptions = FormatOptions.json();
        }
        JobInfo.WriteDisposition writeDisposition = JobInfo.WriteDisposition.WRITE_APPEND;
        if (this.truncateTable)
            writeDisposition = JobInfo.WriteDisposition.WRITE_TRUNCATE;
        LoadJobConfiguration loadConfig = LoadJobConfiguration.newBuilder(tableId, fileList, formatOptions).setWriteDisposition(writeDisposition).build();
        JobInfo jobInfo = JobInfo.of((JobConfiguration) loadConfig);
        Job loadJob = this.bigQuery.create(jobInfo, new BigQuery.JobOption[0]);
        try {
            loadJob = loadJob.waitFor(new com.google.cloud.RetryOption[0]);
            if (loadJob.getStatus().getError() != null) {
                result.setNrErrors(1L);
                result.setResult(false);
                logError("Error while loading table: " + loadJob.getStatus().toString());
            } else {
                result.setResult(true);
            }
        } catch (InterruptedException e) {
            result.setNrErrors(1L);
            result.setResult(false);
            logError("An error occurred executing this job entry : " + e.getMessage());
        }
        return result;
    }



    public void setDatasetName(String dsn) {
        this.datasetName = dsn;
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public void setTableName(String tn) {
        this.tableName = tn;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setSourceUri(String uri) {
        this.sourceUri = uri;
    }

    public String getSourceUri() {
        return this.sourceUri;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String ft) {
        this.fileType = ft;
    }

    public void setDelimiter(String delim) {
        this.delimiter = delim;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setQuote(String q) {
        this.quote = q;
    }

    public String getQuote() {
        return this.quote;
    }

    public void setLeadingRowsToSkip(String skip) {
        this.leadingRowsToSkip = skip;
    }

    public String getLeadingRowsToSkip() {
        return this.leadingRowsToSkip;
    }

    public void setTruncateTable(boolean truncateTable) {
        this.truncateTable = truncateTable;
    }

    public boolean isTruncateTable() {
        return this.truncateTable;
    }

    public void setTableFields(Map<String, String> fs) {
        this.tableFields = fs;
    }

    public Map<String, String> getTableFields() {
        return this.tableFields;
    }

    public void allocate(int nrfields) {
        this.fieldNames = new String[nrfields];
        this.fieldTypes = new String[nrfields];
    }

    public String[] getFieldNames() {
        return this.fieldNames;
    }

    public void setFieldNames(String[] fn) {
        this.fieldNames = fn;
    }

    public String[] getFieldTypes() {
        return this.fieldTypes;
    }

    public void setFieldTypes(String[] t) {
        this.fieldTypes = t;
    }

    public String getConnection() {
        return this.connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public boolean isEnvironmentAuth() {
        return this.isEnvironmentAuth;
    }

    public void setEnvironmentAuth(boolean environmentAuth) {
        this.isEnvironmentAuth = environmentAuth;
    }
}
