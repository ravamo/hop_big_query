/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * http://www.project-hop.org
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.apache.hop.pipeline.transforms.gettablenames;

import org.apache.hop.core.Const;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.PipelinePreviewFactory;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformDialog;
import org.apache.hop.ui.core.dialog.EnterNumberDialog;
import org.apache.hop.ui.core.dialog.EnterTextDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.dialog.PreviewRowsDialog;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.dialog.PipelinePreviewProgressDialog;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

public class GetTableNamesDialog extends BaseTransformDialog implements ITransformDialog {
  private static final Class<?> PKG = GetTableNamesMeta.class; // for i18n purposes, needed by Translator!!

  private MetaSelectionLine<DatabaseMeta> wConnection;

  private Text wTablenameField;
  private Text wSqlCreationField;
  private Button wincludeTable;

  private Button wincludeSchema;

  private Button wincludeCatalog;
  private Label wlincludeCatalog;

  private Button wincludeProcedure;

  private Button wincludeSynonym;

  private Button waddSchemaInOutput;

  private Button wincludeView;

  private Text wObjectTypeField;

  private Text wisSystemObjectField;

  private Label wlschemaname;
  private TextVar wschemaname;

  private Button wdynamicSchema;

  private Label wlSchemaField;
  private CCombo wSchemaField;

  private final GetTableNamesMeta input;

  private boolean gotpreviousfields = false;

  public GetTableNamesDialog( Shell parent, Object in, PipelineMeta pipelineMeta, String sname ) {
    super( parent, (BaseTransformMeta) in, pipelineMeta, sname );
    input = (GetTableNamesMeta) in;
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = e -> input.setChanged();

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = props.getMargin();

    // TransformName line
    wlTransformName = new Label( shell, SWT.RIGHT );
    wlTransformName.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.TransformName.Label" ) );
    props.setLook( wlTransformName );
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment( 0, 0 );
    fdlTransformName.right = new FormAttachment( middle, -margin );
    fdlTransformName.top = new FormAttachment( 0, margin );
    wlTransformName.setLayoutData( fdlTransformName );
    wTransformName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wTransformName.setText( transformName );
    props.setLook( wTransformName );
    wTransformName.addModifyListener( lsMod );
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment( middle, 0 );
    fdTransformName.top = new FormAttachment( 0, margin );
    fdTransformName.right = new FormAttachment( 100, 0 );
    wTransformName.setLayoutData( fdTransformName );

    // Connection line
    wConnection = addConnectionLine( shell, wTransformName, input.getDatabase(), lsMod );

    // schemaname fieldname ...
    wlschemaname = new Label( shell, SWT.RIGHT );
    wlschemaname.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.schemanameName.Label" ) );
    props.setLook( wlschemaname );
    FormData fdlschemaname = new FormData();
    fdlschemaname.left = new FormAttachment( 0, 0 );
    fdlschemaname.right = new FormAttachment( middle, -margin );
    fdlschemaname.top = new FormAttachment( wConnection, 2 * margin );
    wlschemaname.setLayoutData(fdlschemaname);
    wschemaname = new TextVar( pipelineMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wschemaname.setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.schemanameName.Tooltip" ) );
    props.setLook( wschemaname );
    FormData fdschemaname = new FormData();
    fdschemaname.left = new FormAttachment( middle, 0 );
    fdschemaname.top = new FormAttachment( wConnection, 2 * margin );
    fdschemaname.right = new FormAttachment( 100, 0 );
    wschemaname.setLayoutData(fdschemaname);
    ModifyListener lsModSchema = e -> {
      input.setChanged();
      refreshIncludeCatalog();
    };
    wschemaname.addModifyListener( lsModSchema );

    // Is schema name defined in a Field
    Label wldynamicSchema = new Label(shell, SWT.RIGHT);
    wldynamicSchema.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.wldynamicSchema.Label" ) );
    props.setLook(wldynamicSchema);
    FormData fdldynamicSchema = new FormData();
    fdldynamicSchema.left = new FormAttachment( 0, -margin );
    fdldynamicSchema.top = new FormAttachment( wschemaname, margin );
    fdldynamicSchema.right = new FormAttachment( middle, -margin );
    wldynamicSchema.setLayoutData(fdldynamicSchema);

    wdynamicSchema = new Button( shell, SWT.CHECK );
    props.setLook( wdynamicSchema );
    wdynamicSchema.setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.wdynamicSchema.Tooltip" ) );
    FormData fddynamicSchema = new FormData();
    fddynamicSchema.left = new FormAttachment( middle, 0 );
    fddynamicSchema.top = new FormAttachment( wschemaname, margin );
    wdynamicSchema.setLayoutData(fddynamicSchema);
    SelectionAdapter lsxmlstream = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        ActivedynamicSchema();
        input.setChanged();
      }
    };
    wdynamicSchema.addSelectionListener( lsxmlstream );

    // If schema string defined in a Field
    wlSchemaField = new Label( shell, SWT.RIGHT );
    wlSchemaField.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.wlSchemaField.Label" ) );
    props.setLook( wlSchemaField );
    FormData fdlSchemaField = new FormData();
    fdlSchemaField.left = new FormAttachment( 0, -margin );
    fdlSchemaField.top = new FormAttachment( wdynamicSchema, margin );
    fdlSchemaField.right = new FormAttachment( middle, -margin );
    wlSchemaField.setLayoutData(fdlSchemaField);

    wSchemaField = new CCombo( shell, SWT.BORDER | SWT.READ_ONLY );
    wSchemaField.setEditable( true );
    props.setLook( wSchemaField );
    wSchemaField.addModifyListener( lsMod );
    FormData fdSchemaField = new FormData();
    fdSchemaField.left = new FormAttachment( middle, 0 );
    fdSchemaField.top = new FormAttachment( wdynamicSchema, margin );
    fdSchemaField.right = new FormAttachment( 100, -margin );
    wSchemaField.setLayoutData(fdSchemaField);
    wSchemaField.addFocusListener( new FocusListener() {
      public void focusLost( org.eclipse.swt.events.FocusEvent e ) {
      }

      public void focusGained( org.eclipse.swt.events.FocusEvent e ) {
        setSchemaField();
        shell.setCursor( null );
      }
    } );

    // ///////////////////////////////
    // START OF SETTINGS GROUP //
    // ///////////////////////////////

    Group wSettings = new Group(shell, SWT.SHADOW_NONE);
    props.setLook(wSettings);
    wSettings.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.wSettings.Label" ) );

    FormLayout SettingsgroupLayout = new FormLayout();
    SettingsgroupLayout.marginWidth = 10;
    SettingsgroupLayout.marginHeight = 10;
    wSettings.setLayout( SettingsgroupLayout );

    // Include Catalogs
    wlincludeCatalog = new Label(wSettings, SWT.RIGHT );
    wlincludeCatalog.setText( BaseMessages.getString( PKG, "GetCatalogNamesDialog.includeCatalog.Label" ) );
    props.setLook( wlincludeCatalog );
    FormData fdlincludeCatalog = new FormData();
    fdlincludeCatalog.left = new FormAttachment( 0, -margin );
    fdlincludeCatalog.top = new FormAttachment( wSchemaField, margin );
    fdlincludeCatalog.right = new FormAttachment( middle, -2 * margin );
    wlincludeCatalog.setLayoutData(fdlincludeCatalog);

    wincludeCatalog = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeCatalog );
    wincludeCatalog.setToolTipText( BaseMessages.getString( PKG, "GetCatalogNamesDialog.includeCatalog.Tooltip" ) );
    FormData fdincludeCatalog = new FormData();
    fdincludeCatalog.left = new FormAttachment( middle, -margin );
    fdincludeCatalog.top = new FormAttachment( wSchemaField, margin );
    wincludeCatalog.setLayoutData(fdincludeCatalog);
    SelectionAdapter lincludeCatalog = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeCatalog.addSelectionListener( lincludeCatalog );

    // Include Schemas
    Label wlincludeSchema = new Label(wSettings, SWT.RIGHT);
    wlincludeSchema.setText( BaseMessages.getString( PKG, "GetSchemaNamesDialog.includeSchema.Label" ) );
    props.setLook(wlincludeSchema);
    FormData fdlincludeSchema = new FormData();
    fdlincludeSchema.left = new FormAttachment( 0, -margin );
    fdlincludeSchema.top = new FormAttachment( wincludeCatalog, margin );
    fdlincludeSchema.right = new FormAttachment( middle, -2 * margin );
    wlincludeSchema.setLayoutData(fdlincludeSchema);

    wincludeSchema = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeSchema );
    wincludeSchema.setToolTipText( BaseMessages.getString( PKG, "GetSchemaNamesDialog.includeSchema.Tooltip" ) );
    FormData fdincludeSchema = new FormData();
    fdincludeSchema.left = new FormAttachment( middle, -margin );
    fdincludeSchema.top = new FormAttachment( wincludeCatalog, margin );
    wincludeSchema.setLayoutData(fdincludeSchema);
    SelectionAdapter lincludeSchema = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeSchema.addSelectionListener( lincludeSchema );

    // Include tables
    Label wlincludeTable = new Label(wSettings, SWT.RIGHT);
    wlincludeTable.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeTable.Label" ) );
    props.setLook(wlincludeTable);
    FormData fdlincludeTable = new FormData();
    fdlincludeTable.left = new FormAttachment( 0, -margin );
    fdlincludeTable.top = new FormAttachment( wincludeSchema, margin );
    fdlincludeTable.right = new FormAttachment( middle, -2 * margin );
    wlincludeTable.setLayoutData(fdlincludeTable);

    wincludeTable = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeTable );
    wincludeTable.setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeTable.Tooltip" ) );
    FormData fdincludeTable = new FormData();
    fdincludeTable.left = new FormAttachment( middle, -margin );
    fdincludeTable.top = new FormAttachment( wincludeSchema, margin );
    wincludeTable.setLayoutData(fdincludeTable);
    SelectionAdapter lincludeTable = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeTable.addSelectionListener( lincludeTable );

    // Include views
    Label wlincludeView = new Label(wSettings, SWT.RIGHT);
    wlincludeView.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeView.Label" ) );
    props.setLook(wlincludeView);
    FormData fdlincludeView = new FormData();
    fdlincludeView.left = new FormAttachment( 0, -margin );
    fdlincludeView.top = new FormAttachment( wincludeTable, margin );
    fdlincludeView.right = new FormAttachment( middle, -2 * margin );
    wlincludeView.setLayoutData(fdlincludeView);

    wincludeView = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeView );
    wincludeView.setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeView.Tooltip" ) );
    FormData fdincludeView = new FormData();
    fdincludeView.left = new FormAttachment( middle, -margin );
    fdincludeView.top = new FormAttachment( wincludeTable, margin );
    wincludeView.setLayoutData(fdincludeView);
    SelectionAdapter lincludeView = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeView.addSelectionListener( lincludeView );

    // Include procedures
    Label wlincludeProcedure = new Label(wSettings, SWT.RIGHT);
    wlincludeProcedure.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeProcedure.Label" ) );
    props.setLook(wlincludeProcedure);
    FormData fdlincludeProcedure = new FormData();
    fdlincludeProcedure.left = new FormAttachment( 0, -margin );
    fdlincludeProcedure.top = new FormAttachment( wincludeView, margin );
    fdlincludeProcedure.right = new FormAttachment( middle, -2 * margin );
    wlincludeProcedure.setLayoutData(fdlincludeProcedure);

    wincludeProcedure = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeProcedure );
    wincludeProcedure
      .setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeProcedure.Tooltip" ) );
    FormData fdincludeProcedure = new FormData();
    fdincludeProcedure.left = new FormAttachment( middle, -margin );
    fdincludeProcedure.top = new FormAttachment( wincludeView, margin );
    wincludeProcedure.setLayoutData(fdincludeProcedure);
    SelectionAdapter lincludeProcedure = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeProcedure.addSelectionListener( lincludeProcedure );

    // Include Synonyms
    Label wlincludeSynonym = new Label(wSettings, SWT.RIGHT);
    wlincludeSynonym.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeSynonym.Label" ) );
    props.setLook(wlincludeSynonym);
    FormData fdlincludeSynonym = new FormData();
    fdlincludeSynonym.left = new FormAttachment( 0, -margin );
    fdlincludeSynonym.top = new FormAttachment( wincludeProcedure, margin );
    fdlincludeSynonym.right = new FormAttachment( middle, -2 * margin );
    wlincludeSynonym.setLayoutData(fdlincludeSynonym);

    wincludeSynonym = new Button(wSettings, SWT.CHECK );
    props.setLook( wincludeSynonym );
    wincludeSynonym.setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.includeSynonym.Tooltip" ) );
    FormData fdincludeSynonym = new FormData();
    fdincludeSynonym.left = new FormAttachment( middle, -margin );
    fdincludeSynonym.top = new FormAttachment( wincludeProcedure, margin );
    wincludeSynonym.setLayoutData(fdincludeSynonym);
    SelectionAdapter lincludeSynonym = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wincludeSynonym.addSelectionListener( lincludeSynonym );

    // Add schema in output
    Label wladdSchemaInOutput = new Label(wSettings, SWT.RIGHT);
    wladdSchemaInOutput.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.addSchemaInOutput.Label" ) );
    props.setLook(wladdSchemaInOutput);
    FormData fdladdSchemaInOutput = new FormData();
    fdladdSchemaInOutput.left = new FormAttachment( 0, -margin );
    fdladdSchemaInOutput.top = new FormAttachment( wincludeSynonym, 2 * margin );
    fdladdSchemaInOutput.right = new FormAttachment( middle, -2 * margin );
    wladdSchemaInOutput.setLayoutData(fdladdSchemaInOutput);

    waddSchemaInOutput = new Button(wSettings, SWT.CHECK );
    props.setLook( waddSchemaInOutput );
    waddSchemaInOutput.setToolTipText( BaseMessages.getString(
      PKG, "GetTableNamesDialog.addSchemaInOutput.Tooltip" ) );
    FormData fdaddSchemaInOutput = new FormData();
    fdaddSchemaInOutput.left = new FormAttachment( middle, -margin );
    fdaddSchemaInOutput.top = new FormAttachment( wincludeSynonym, 2 * margin );
    waddSchemaInOutput.setLayoutData(fdaddSchemaInOutput);
    SelectionAdapter laddSchemaInOutput = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    waddSchemaInOutput.addSelectionListener( laddSchemaInOutput );

    FormData fdSettings = new FormData();
    fdSettings.left = new FormAttachment( 0, margin );
    fdSettings.top = new FormAttachment( wSchemaField, 2 * margin );
    fdSettings.right = new FormAttachment( 100, -margin );
    wSettings.setLayoutData( fdSettings );

    // ///////////////////////////////////////////////////////////
    // / END OF SETTINGS GROUP
    // ///////////////////////////////////////////////////////////

    // ///////////////////////////////
    // START OF OutputFields GROUP //
    // ///////////////////////////////

    Group wOutputFields = new Group(shell, SWT.SHADOW_NONE);
    props.setLook(wOutputFields);
    wOutputFields.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.wOutputFields.Label" ) );

    FormLayout OutputFieldsgroupLayout = new FormLayout();
    OutputFieldsgroupLayout.marginWidth = 10;
    OutputFieldsgroupLayout.marginHeight = 10;
    wOutputFields.setLayout( OutputFieldsgroupLayout );

    // TablenameField fieldname ...
    Label wlTablenameField = new Label(wOutputFields, SWT.RIGHT);
    wlTablenameField.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.TablenameFieldName.Label" ) );
    props.setLook(wlTablenameField);
    FormData fdlTablenameField = new FormData();
    fdlTablenameField.left = new FormAttachment( 0, 0 );
    fdlTablenameField.right = new FormAttachment( middle, -margin );
    fdlTablenameField.top = new FormAttachment(wSettings, margin * 2 );
    wlTablenameField.setLayoutData(fdlTablenameField);
    wTablenameField = new Text(wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wTablenameField
      .setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.TablenameFieldName.Tooltip" ) );
    props.setLook( wTablenameField );
    wTablenameField.addModifyListener( lsMod );
    FormData fdTablenameField = new FormData();
    fdTablenameField.left = new FormAttachment( middle, 0 );
    fdTablenameField.top = new FormAttachment(wSettings, margin * 2 );
    fdTablenameField.right = new FormAttachment( 100, 0 );
    wTablenameField.setLayoutData(fdTablenameField);

    // ObjectTypeField fieldname ...
    Label wlObjectTypeField = new Label(wOutputFields, SWT.RIGHT);
    wlObjectTypeField.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectTypeFieldName.Label" ) );
    props.setLook(wlObjectTypeField);
    FormData fdlObjectTypeField = new FormData();
    fdlObjectTypeField.left = new FormAttachment( 0, 0 );
    fdlObjectTypeField.right = new FormAttachment( middle, -margin );
    fdlObjectTypeField.top = new FormAttachment( wTablenameField, margin );
    wlObjectTypeField.setLayoutData(fdlObjectTypeField);
    wObjectTypeField = new Text(wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wObjectTypeField.setToolTipText( BaseMessages.getString(
      PKG, "GetTableNamesDialog.ObjectTypeFieldName.Tooltip" ) );
    props.setLook( wObjectTypeField );
    wObjectTypeField.addModifyListener( lsMod );
    FormData fdObjectTypeField = new FormData();
    fdObjectTypeField.left = new FormAttachment( middle, 0 );
    fdObjectTypeField.top = new FormAttachment( wTablenameField, margin );
    fdObjectTypeField.right = new FormAttachment( 100, 0 );
    wObjectTypeField.setLayoutData(fdObjectTypeField);

    // isSystemObjectField fieldname ...
    Label wlisSystemObjectField = new Label(wOutputFields, SWT.RIGHT);
    wlisSystemObjectField.setText( BaseMessages.getString(
      PKG, "GetTableNamesDialog.isSystemObjectFieldName.Label" ) );
    props.setLook(wlisSystemObjectField);
    FormData fdlisSystemObjectField = new FormData();
    fdlisSystemObjectField.left = new FormAttachment( 0, 0 );
    fdlisSystemObjectField.right = new FormAttachment( middle, -margin );
    fdlisSystemObjectField.top = new FormAttachment( wObjectTypeField, margin );
    wlisSystemObjectField.setLayoutData(fdlisSystemObjectField);
    wisSystemObjectField = new Text(wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wisSystemObjectField.setToolTipText( BaseMessages.getString(
      PKG, "GetTableNamesDialog.isSystemObjectFieldName.Tooltip" ) );
    props.setLook( wisSystemObjectField );
    wisSystemObjectField.addModifyListener( lsMod );
    FormData fdisSystemObjectField = new FormData();
    fdisSystemObjectField.left = new FormAttachment( middle, 0 );
    fdisSystemObjectField.top = new FormAttachment( wObjectTypeField, margin );
    fdisSystemObjectField.right = new FormAttachment( 100, 0 );
    wisSystemObjectField.setLayoutData(fdisSystemObjectField);

    // CreationSQL fieldname ...
    Label wlSqlCreationField = new Label(wOutputFields, SWT.RIGHT);
    wlSqlCreationField.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.CreationSQLName.Label" ) );
    props.setLook(wlSqlCreationField);
    FormData fdlSqlCreationField = new FormData();
    fdlSqlCreationField.left = new FormAttachment( 0, 0 );
    fdlSqlCreationField.right = new FormAttachment( middle, -margin );
    fdlSqlCreationField.top = new FormAttachment( wisSystemObjectField, margin );
    wlSqlCreationField.setLayoutData(fdlSqlCreationField);
    wSqlCreationField = new Text(wOutputFields, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wSqlCreationField
      .setToolTipText( BaseMessages.getString( PKG, "GetTableNamesDialog.CreationSQLName.Tooltip" ) );
    props.setLook( wSqlCreationField );
    wSqlCreationField.addModifyListener( lsMod );
    FormData fdSqlCreationField = new FormData();
    fdSqlCreationField.left = new FormAttachment( middle, 0 );
    fdSqlCreationField.top = new FormAttachment( wisSystemObjectField, margin );
    fdSqlCreationField.right = new FormAttachment( 100, 0 );
    wSqlCreationField.setLayoutData(fdSqlCreationField);

    FormData fdOutputFields = new FormData();
    fdOutputFields.left = new FormAttachment( 0, margin );
    fdOutputFields.top = new FormAttachment(wSettings, 2 * margin );
    fdOutputFields.right = new FormAttachment( 100, -margin );
    wOutputFields.setLayoutData( fdOutputFields );

    // ///////////////////////////////////////////////////////////
    // / END OF OutputFields GROUP
    // ///////////////////////////////////////////////////////////

    // THE BUTTONS
    wOk = new Button( shell, SWT.PUSH );
    wOk.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );

    wPreview = new Button( shell, SWT.PUSH );
    wPreview.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.Preview.Button" ) );

    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOk, wPreview, wCancel }, margin, wOutputFields);

    // Add listeners
    lsOk = e -> ok();

    lsCancel = e -> cancel();
    lsPreview = e -> preview();
    wOk.addListener( SWT.Selection, lsOk );
    wPreview.addListener( SWT.Selection, lsPreview );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wTransformName.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    ActivedynamicSchema();
    refreshIncludeCatalog();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return transformName;
  }

  private void refreshIncludeCatalog() {
    if ( !Utils.isEmpty( wschemaname.getText() ) ) {
      wincludeCatalog.setSelection( false );
      wlincludeCatalog.setEnabled( false );
      wincludeCatalog.setEnabled( false );
    } else {
      wlincludeCatalog.setEnabled( true );
      wincludeCatalog.setEnabled( true );
    }
  }

  private void ActivedynamicSchema() {
    wlSchemaField.setEnabled( wdynamicSchema.getSelection() );
    wSchemaField.setEnabled( wdynamicSchema.getSelection() );
    wPreview.setEnabled( !wdynamicSchema.getSelection() );
    wlschemaname.setEnabled( !wdynamicSchema.getSelection() );
    wschemaname.setEnabled( !wdynamicSchema.getSelection() );
    if ( wdynamicSchema.getSelection() ) {
      wincludeCatalog.setSelection( false );
    }
    wlincludeCatalog.setEnabled( !wdynamicSchema.getSelection() );
    wincludeCatalog.setEnabled( !wdynamicSchema.getSelection() );
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( isDebug() ) {
      logDebug( toString(), BaseMessages.getString( PKG, "GetTableNamesDialog.Log.GettingKeyInfo" ) );
    }

    if ( input.getDatabase() != null ) {
      wConnection.setText( input.getDatabase().getName() );
    }
    if ( input.getSchemaName() != null ) {
      wschemaname.setText( input.getSchemaName() );
    }
    if ( input.getTablenameFieldName() != null ) {
      wTablenameField.setText( input.getTablenameFieldName() );
    }
    if ( input.getObjectTypeFieldName() != null ) {
      wObjectTypeField.setText( input.getObjectTypeFieldName() );
    }
    if ( input.isSystemObjectFieldName() != null ) {
      wisSystemObjectField.setText( input.isSystemObjectFieldName() );
    }
    if ( input.getSqlCreationFieldName() != null ) {
      wSqlCreationField.setText( input.getSqlCreationFieldName() );
    }
    wincludeCatalog.setSelection( input.isIncludeCatalog() );
    wincludeSchema.setSelection( input.isIncludeSchema() );
    wincludeTable.setSelection( input.isIncludeTable() );
    wincludeView.setSelection( input.isIncludeView() );
    wincludeProcedure.setSelection( input.isIncludeProcedure() );
    wincludeSynonym.setSelection( input.isIncludeSynonym() );
    waddSchemaInOutput.setSelection( input.isAddSchemaInOut() );

    wdynamicSchema.setSelection( input.isDynamicSchema() );
    if ( input.getSchemaFieldName() != null ) {
      wSchemaField.setText( input.getSchemaFieldName() );
    }

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged( changed );
    dispose();
  }

  private void setSchemaField() {
    if ( !gotpreviousfields ) {
      try {
        String value = wSchemaField.getText();
        wSchemaField.removeAll();

        IRowMeta r = pipelineMeta.getPrevTransformFields( transformName );
        if ( r != null ) {
          wSchemaField.setItems( r.getFieldNames() );
        }
        if ( value != null ) {
          wSchemaField.setText( value );
        }
      } catch ( HopException ke ) {
        new ErrorDialog(
          shell, BaseMessages.getString( PKG, "GetTableNamesDialog.FailedToGetFields.DialogTitle" ),
          BaseMessages.getString( PKG, "GetTableNamesDialog.FailedToGetFields.DialogMessage" ), ke );
      }
      gotpreviousfields = true;
    }
  }

  private void ok() {
    if ( Utils.isEmpty( wTransformName.getText() ) ) {
      return;
    }
    transformName = wTransformName.getText(); // return value
    getInfo( input );
    if ( input.getDatabase() == null ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setMessage( BaseMessages.getString( PKG, "GetTableNamesDialog.InvalidConnection.DialogMessage" ) );
      mb.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.InvalidConnection.DialogTitle" ) );
      mb.open();
      return;
    }
    dispose();
  }

  private void getInfo( GetTableNamesMeta info ) {
    info.setDatabase( pipelineMeta.findDatabase( wConnection.getText() ) );
    info.setSchemaName( wschemaname.getText() );
    info.setTablenameFieldName( wTablenameField.getText() );
    info.setSqlCreationFieldName( wSqlCreationField.getText() );
    info.setObjectTypeFieldName( wObjectTypeField.getText() );
    info.setIsSystemObjectFieldName( wisSystemObjectField.getText() );
    info.setIncludeCatalog( wincludeCatalog.getSelection() );
    info.setIncludeSchema( wincludeSchema.getSelection() );
    info.setIncludeTable( wincludeTable.getSelection() );
    info.setIncludeView( wincludeView.getSelection() );
    info.setIncludeProcedure( wincludeProcedure.getSelection() );
    info.setIncludeSynonym( wincludeSynonym.getSelection() );
    info.setAddSchemaInOut( waddSchemaInOutput.getSelection() );

    info.setDynamicSchema( wdynamicSchema.getSelection() );
    info.setSchemaFieldName( wSchemaField.getText() );

  }

  private boolean checkUserInput( GetTableNamesMeta meta ) {

    if ( Utils.isEmpty( meta.getTablenameFieldName() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setMessage( BaseMessages.getString( PKG, "GetTableNamesDialog.Error.TablenameFieldNameMissingMessage" ) );
      mb.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.Error.TablenameFieldNameMissingTitle" ) );
      mb.open();

      return false;
    }
    return true;
  }

  // Preview the data
  private void preview() {
    GetTableNamesMeta oneMeta = new GetTableNamesMeta();

    getInfo( oneMeta );
    if ( oneMeta.getDatabase() == null ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setMessage( BaseMessages.getString( PKG, "GetTableNamesDialog.InvalidConnection.DialogMessage" ) );
      mb.setText( BaseMessages.getString( PKG, "GetTableNamesDialog.InvalidConnection.DialogTitle" ) );
      mb.open();
      return;
    }
    if ( !checkUserInput( oneMeta ) ) {
      return;
    }

    PipelineMeta previewMeta = PipelinePreviewFactory.generatePreviewPipeline( pipelineMeta, pipelineMeta.getMetadataProvider(),
      oneMeta, wTransformName.getText() );

    EnterNumberDialog numberDialog = new EnterNumberDialog( shell, props.getDefaultPreviewSize(),
      BaseMessages.getString( PKG, "GetTableNamesDialog.PreviewSize.DialogTitle" ),
      BaseMessages.getString( PKG, "GetTableNamesDialog.PreviewSize.DialogMessage" ) );
    int previewSize = numberDialog.open();
    if ( previewSize > 0 ) {
      PipelinePreviewProgressDialog progressDialog =
        new PipelinePreviewProgressDialog(
          shell, previewMeta, new String[] { wTransformName.getText() }, new int[] { previewSize } );
      progressDialog.open();

      if ( !progressDialog.isCancelled() ) {
        Pipeline pipeline = progressDialog.getPipeline();
        String loggingText = progressDialog.getLoggingText();

        if ( pipeline.getResult() != null && pipeline.getResult().getNrErrors() > 0 ) {
          EnterTextDialog etd =
            new EnterTextDialog( shell, BaseMessages.getString( PKG, "System.Dialog.Error.Title" ), BaseMessages
              .getString( PKG, "GetTableNamesDialog.ErrorInPreview.DialogMessage" ), loggingText, true );
          etd.setReadOnly();
          etd.open();
        }

        PreviewRowsDialog prd =
          new PreviewRowsDialog(
            shell, pipelineMeta, SWT.NONE, wTransformName.getText(), progressDialog.getPreviewRowsMeta( wTransformName
            .getText() ), progressDialog.getPreviewRows( wTransformName.getText() ), loggingText );
        prd.open();
      }
    }
  }
}
