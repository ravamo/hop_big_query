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

package org.apache.hop.pipeline.engine;

import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.plugins.BasePluginType;
import org.apache.hop.core.plugins.IPluginType;
import org.apache.hop.core.plugins.PluginAnnotationType;
import org.apache.hop.core.plugins.PluginFolder;
import org.apache.hop.core.plugins.PluginMainClassType;

import java.util.Map;

@PluginMainClassType( IPipelineEngine.class )
@PluginAnnotationType( PipelineEnginePlugin.class )
public class PipelineEnginePluginType extends BasePluginType<PipelineEnginePlugin> implements IPluginType<PipelineEnginePlugin> {

  private PipelineEnginePluginType() {
    super( PipelineEnginePlugin.class, "HOP_PIPELINE_ENGINES", "Hop Pipeline Engines" );

    pluginFolders.add( new PluginFolder( "plugins", false, true ) );
  }

  private static PipelineEnginePluginType pluginType;

  public static PipelineEnginePluginType getInstance() {
    if ( pluginType == null ) {
      pluginType = new PipelineEnginePluginType();
    }
    return pluginType;
  }

  @Override
  protected void registerNatives() throws HopPluginException {
    super.registerNatives();
  }

  @Override
  protected String getXmlPluginFile() {
    return Const.XML_FILE_HOP_PIPELINE_ENGINES;
  }

  @Override public String getMainTag() {
    return "hop-pipeline-engines";
  }

  @Override public String getSubTag() {
    return "hop-pipeline-engine";
  }

  @Override
  protected String getPath() {
    return "./";
  }

  @Override
  protected String extractCategory( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected String extractDesc( PipelineEnginePlugin annotation ) {
    return ( (PipelineEnginePlugin) annotation ).description();
  }

  @Override
  protected String extractID( PipelineEnginePlugin annotation ) {
    return ( (PipelineEnginePlugin) annotation ).id();
  }

  @Override
  protected String extractName( PipelineEnginePlugin annotation ) {
    return ( (PipelineEnginePlugin) annotation ).name();
  }

  @Override
  protected String extractImageFile( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected boolean extractSeparateClassLoader( PipelineEnginePlugin annotation ) {
    return false;
  }

  @Override
  protected String extractI18nPackageName( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected void addExtraClasses( Map<Class<?>, String> classMap, Class<?> clazz, PipelineEnginePlugin annotation ) {
  }

  @Override
  protected String extractDocumentationUrl( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected String extractCasesUrl( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected String extractForumUrl( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected String extractSuggestion( PipelineEnginePlugin annotation ) {
    return null;
  }

  @Override
  protected String extractClassLoaderGroup( PipelineEnginePlugin annotation ) {
    return null;
  }
}