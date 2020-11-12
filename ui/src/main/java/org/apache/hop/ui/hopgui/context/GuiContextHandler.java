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

package org.apache.hop.ui.hopgui.context;

import org.apache.hop.core.gui.plugin.action.GuiAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles actions for a certain context.
 * For example, the main HopGui dialog registers a bunch of context handlers for MetaStore objects, asks the various perspectives, ...
 */
public class GuiContextHandler implements IGuiContextHandler {
  private List<GuiAction> supportedActions;

  public GuiContextHandler() {
    supportedActions = new ArrayList<>();
  }

  /**
   * Gets supportedActions
   *
   * @return value of supportedActions
   */
  @Override public List<GuiAction> getSupportedActions() {
    return supportedActions;
  }

  /**
   * @param supportedActions The supportedActions to set
   */
  public void setSupportedActions( List<GuiAction> supportedActions ) {
    this.supportedActions = supportedActions;
  }
}
