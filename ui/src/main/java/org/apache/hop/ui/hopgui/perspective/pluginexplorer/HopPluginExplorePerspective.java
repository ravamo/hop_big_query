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

package org.apache.hop.ui.hopgui.perspective.pluginexplorer;

import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.gui.plugin.GuiPlugin;
import org.apache.hop.core.gui.plugin.toolbar.GuiToolbarElement;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.RowBuffer;
import org.apache.hop.core.search.ISearchable;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.ui.core.ConstUi;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.gui.GuiResource;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.hopgui.HopGui;
import org.apache.hop.ui.hopgui.context.IGuiContextHandler;
import org.apache.hop.ui.hopgui.file.IHopFileType;
import org.apache.hop.ui.hopgui.file.IHopFileTypeHandler;
import org.apache.hop.ui.hopgui.perspective.HopPerspectivePlugin;
import org.apache.hop.ui.hopgui.perspective.IHopPerspective;
import org.apache.hop.ui.hopgui.perspective.TabItemHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HopPerspectivePlugin(id = "Hop-Plugin-Explorer-Perspective", name = "Plugin explorer", description = "The Hop Plugin Explorer Perspective")
@GuiPlugin
public class HopPluginExplorePerspective implements IHopPerspective {

	public static final String ID_PERSPECTIVE_TOOLBAR_ITEM = "20030-perspective-plugins";

	private HopGui hopGui;
	private Composite parent;
	private Composite composite;
	private CCombo wPluginType;
	private TableView wPluginView;
	private FormData formData;

	private Map<String, List<Object[]>> dataMap;
	private Map<String, IRowMeta> metaMap;

	private String[] pluginsType;
	private String selectedPluginType;

	private Image pluginImage;
	private Image pluginDisabledImage;


	public HopPluginExplorePerspective() {
	}

	@Override public String getId() {
		return "plugin-explorer";
	}

	@GuiToolbarElement(
		root = HopGui.GUI_PLUGIN_PERSPECTIVES_PARENT_ID,
		id = ID_PERSPECTIVE_TOOLBAR_ITEM,
		image = "ui/images/Plugin.svg",
		toolTip = "Explore plugin"
	)
	public void activate() {
		hopGui.getPerspectiveManager().showPerspective( this.getClass() );
	}

	@Override
	public void show() {
		composite.setVisible( true );
		if ( pluginImage == null ) {
			pluginImage = GuiResource.getInstance().loadAsResource( hopGui.getDisplay(), "ui/images/Plugin.svg", ConstUi.SMALL_ICON_SIZE );
		}
		hopGui.getPerspectivesToolbarWidgets().findToolItem( ID_PERSPECTIVE_TOOLBAR_ITEM ).setImage( pluginImage );
	}

	@Override
	public void hide() {
		composite.setVisible( false );
		if ( pluginDisabledImage == null ) {
			pluginDisabledImage = GuiResource.getInstance().loadAsResource( hopGui.getDisplay(), "ui/images/Plugin_inactive.svg", ConstUi.SMALL_ICON_SIZE );
		}
		hopGui.getPerspectivesToolbarWidgets().findToolItem( ID_PERSPECTIVE_TOOLBAR_ITEM ).setImage( pluginDisabledImage );
	}

	@Override
	public boolean isActive() {
		return composite != null && !composite.isDisposed() && composite.isVisible();
	}

	@Override
	public IHopFileTypeHandler getActiveFileTypeHandler() {
		return null; // Not handling anything really
	}

	@Override public void setActiveFileTypeHandler( IHopFileTypeHandler activeFileTypeHandler ) {
	}

	@Override
	public List<IHopFileType> getSupportedHopFileTypes() {
		return Collections.emptyList();
	}

	@Override
	public void initialize( HopGui hopGui, Composite parent ) {
		this.hopGui = hopGui;
		this.parent = parent;

		this.loadPlugin();

		PropsUi props = PropsUi.getInstance();

		composite = new Composite( parent, SWT.NONE );
		composite.setLayout( new FormLayout() );

		formData = new FormData();
		formData.left = new FormAttachment( 0, 0 );
		formData.top = new FormAttachment( 0, 0 );
		formData.right = new FormAttachment( 100, 0 );
		formData.bottom = new FormAttachment( 100, 0 );
		composite.setLayoutData( formData );

		Label label = new Label( composite, SWT.LEFT );
		label.setText( "Plugin type" );
		FormData fdlFields = new FormData();
		fdlFields.left = new FormAttachment( 0, 0 );
		fdlFields.top = new FormAttachment( 0, props.getMargin() );
		label.setLayoutData( fdlFields );

		wPluginType = new CCombo( composite, SWT.LEFT | SWT.READ_ONLY | SWT.BORDER );
		wPluginType.setItems( pluginsType );
		wPluginType.setText( selectedPluginType );
		props.setLook( wPluginType );
		FormData fdlSubject = new FormData();
		fdlSubject.left = new FormAttachment( label, props.getMargin() );
		fdlSubject.top = new FormAttachment( label, 0, SWT.CENTER );
		wPluginType.setLayoutData( fdlSubject );

		wPluginType.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent arg0 ) {
				selectedPluginType = wPluginType.getText();
				refresh();
			}
		} );

		IRowMeta rowMeta = metaMap.get( selectedPluginType );
		ColumnInfo[] colinf = new ColumnInfo[ rowMeta.size() ];
		for ( int i = 0; i < rowMeta.size(); i++ ) {
			IValueMeta v = rowMeta.getValueMeta( i );
			colinf[ i ] = new ColumnInfo( v.getName(), ColumnInfo.COLUMN_TYPE_TEXT, v.isNumeric() );
			colinf[ i ].setToolTip( v.toStringMeta() );
			colinf[ i ].setValueMeta( v );
		}

		wPluginView = new TableView( new Variables(), composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, 0,
			null, props );
		wPluginView.setShowingBlueNullValues( true );

		FormData fdFields = new FormData();
		fdFields.left = new FormAttachment( 0, 0 );
		fdFields.top = new FormAttachment( wPluginType, props.getMargin() );
		fdFields.right = new FormAttachment( 100, 0 );
		fdFields.bottom = new FormAttachment( 100, 0 );
		wPluginView.setLayoutData( fdFields );

		this.refresh();
	}

	private void loadPlugin() {
		// First we collect information concerning all the plugin types...
		try {
			metaMap = new HashMap<>();
			dataMap = new HashMap<>();
			PluginRegistry registry = PluginRegistry.getInstance();
			List<Class<? extends IPluginType>> pluginTypeClasses = registry.getPluginTypes();
			for ( Class<? extends IPluginType> pluginTypeClass : pluginTypeClasses ) {
				IPluginType pluginTypeInterface = registry.getPluginType( pluginTypeClass );
				if ( pluginTypeInterface.isFragment() ) {
					continue;
				}
				String name = pluginTypeInterface.getName();
				RowBuffer pluginInformation = registry.getPluginInformation( pluginTypeClass );
				metaMap.put( name, pluginInformation.getRowMeta() );
				dataMap.put( name, pluginInformation.getBuffer() );
			}

			this.pluginsType = metaMap.keySet().toArray( new String[ metaMap.size() ] );
			Arrays.sort( pluginsType );

			selectedPluginType = "";
			if ( !metaMap.isEmpty() ) {
				selectedPluginType = pluginsType[ 0 ];
			}
		} catch ( HopPluginException e ) {
			new ErrorDialog( hopGui.getShell(), "Error", "Error collect plugins", e );
		}
	}

	protected void refresh() {

		wPluginView.clearAll();

		// Add the data rows...
		IRowMeta rowMeta = metaMap.get( selectedPluginType );
		List<Object[]> buffer = dataMap.get( selectedPluginType );

		Table table = wPluginView.getTable();
		table.setRedraw( false );

		for ( int i = 0; i < buffer.size(); i++ ) {
			TableItem item;
			if ( i == 0 ) {
				item = table.getItem( i );
			} else {
				item = new TableItem( table, SWT.NONE );
			}

			Object[] row = buffer.get( i );

			// Display line number
			item.setText( 0, Integer.toString( i + 1 ) );

			// Display plugins infos
			for ( int column = 0; column < rowMeta.size(); column++ ) {
				try {
					IValueMeta vm = rowMeta.getValueMeta( column );
					String value = vm.getString( row[ column ] );

					if ( value != null ) {
						item.setText( column + 1, value );
						item.setForeground( column + 1, GuiResource.getInstance().getColorBlack() );
					}
				} catch ( HopValueException e ) {
					// Ignore
				}
			}
		}

		if ( !wPluginView.isDisposed() ) {
			wPluginView.optWidth( true, buffer.size() );
			table.setRedraw( true );
		}
	}

	@Override
	public boolean remove( IHopFileTypeHandler typeHandler ) {
		return false; // Nothing to do here
	}

	@Override public List<TabItemHandler> getItems() {
		return null;
	}

	@Override
	public void navigateToPreviousFile() {

	}

	@Override
	public void navigateToNextFile() {

	}

	@Override
	public boolean hasNavigationPreviousFile() {
		return false;
	}

	@Override
	public boolean hasNavigationNextFile() {
		return false;
	}

	/**
	 * Gets hopGui
	 *
	 * @return value of hopGui
	 */
	public HopGui getHopGui() {
		return hopGui;
	}

	/**
	 * @param hopGui The hopGui to set
	 */
	public void setHopGui( HopGui hopGui ) {
		this.hopGui = hopGui;
	}

	/**
	 * Gets parent
	 *
	 * @return value of parent
	 */
	public Composite getParent() {
		return parent;
	}

	/**
	 * @param parent The parent to set
	 */
	public void setParent( Composite parent ) {
		this.parent = parent;
	}

	/**
	 * Gets composite
	 *
	 * @return value of composite
	 */
	@Override
	public Composite getComposite() {
		return composite;
	}

	/**
	 * @param composite The composite to set
	 */
	public void setComposite( Composite composite ) {
		this.composite = composite;
	}

	/**
	 * Gets formData
	 *
	 * @return value of formData
	 */
	@Override
	public FormData getFormData() {
		return formData;
	}

	@Override
	public List<IGuiContextHandler> getContextHandlers() {
		List<IGuiContextHandler> handlers = new ArrayList<>();
		return handlers;
	}

	@Override public List<ISearchable> getSearchables() {
		List<ISearchable> searchables = new ArrayList<>();
		return searchables;
	}
}