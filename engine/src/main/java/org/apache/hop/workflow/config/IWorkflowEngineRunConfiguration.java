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

package org.apache.hop.workflow.config;

import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadataObject;

@HopMetadataObject(
  objectFactory = WorkflowRunConfigurationMetadataObjectFactory.class
)
public interface IWorkflowEngineRunConfiguration extends Cloneable, IVariables {

  IWorkflowEngineRunConfiguration clone();

  void setEnginePluginId( String pluginId );

  String getEnginePluginId();

  void setEnginePluginName( String name );

  String getEnginePluginName();
}