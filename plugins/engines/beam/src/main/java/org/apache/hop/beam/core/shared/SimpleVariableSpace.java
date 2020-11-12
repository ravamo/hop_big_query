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

package org.apache.hop.beam.core.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A variablespace which simply is easy to serialize.
 */
public class SimpleVariableSpace implements Serializable {

  private List<VariableValue> variables;

  public SimpleVariableSpace() {
    variables = new ArrayList<>();
  }

  /**
   * Gets variables
   *
   * @return value of variables
   */
  public List<VariableValue> getVariables() {
    return variables;
  }

  /**
   * @param variables The variables to set
   */
  public void setVariables( List<VariableValue> variables ) {
    this.variables = variables;
  }
}