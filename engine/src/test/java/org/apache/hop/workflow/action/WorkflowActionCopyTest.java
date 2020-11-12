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

package org.apache.hop.workflow.action;

import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.actions.special.ActionSpecial;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class WorkflowActionCopyTest {

  private static final String ATTRIBUTE_GROUP = "aGroupName";
  private static final String ATTRIBUTE_KEY = "someKey";
  private static final String ATTRIBUTE_VALUE = "aValue";
  private ActionCopy originJobEntry;
  private ActionCopy copyJobEntry;
  private IAction originEntry;

  @Before
  public void setUp() throws Exception {
    originJobEntry = new ActionCopy();
    copyJobEntry = new ActionCopy();

    originEntry = new ActionSpecial( "EntrySpecial", false, false );
    originEntry.setChanged( false );

    originJobEntry.setAction( originEntry );
    originJobEntry.setAttribute( ATTRIBUTE_GROUP, ATTRIBUTE_KEY, ATTRIBUTE_VALUE );
  }

  @Test
  public void testReplaceMetaCloneEntryOfOrigin() throws Exception {

    copyJobEntry.replaceMeta( originJobEntry );
    assertNotSame( "Entry of origin and copy Action should be different objects: ", copyJobEntry.getAction(),
      originJobEntry.getAction() );
  }

  @Test
  public void testReplaceMetaDoesNotChangeEntryOfOrigin() throws Exception {

    copyJobEntry.replaceMeta( originJobEntry );
    assertEquals( "hasChanged in Entry of origin Action should not be changed. ", false, originJobEntry.getAction()
      .hasChanged() );
  }

  @Test
  public void testReplaceMetaChangesEntryOfCopy() throws Exception {

    copyJobEntry.replaceMeta( originJobEntry );
    assertEquals( "hasChanged in Entry of copy Action should be changed. ", true, copyJobEntry.getAction()
      .hasChanged() );
  }

  @Test
  public void testSetParentMeta() throws Exception {
    WorkflowMeta meta = Mockito.mock( WorkflowMeta.class );
    originJobEntry.setParentWorkflowMeta( meta );
    assertEquals( meta, originEntry.getParentWorkflowMeta() );
  }

  @Test
  public void testCloneClonesAttributesMap() throws Exception {

    ActionCopy clonedJobEntry = (ActionCopy) originJobEntry.clone();
    assertNotNull( clonedJobEntry.getAttributesMap() );
    assertEquals( originJobEntry.getAttribute( ATTRIBUTE_GROUP, ATTRIBUTE_KEY ),
      clonedJobEntry.getAttribute( ATTRIBUTE_GROUP, ATTRIBUTE_KEY ) );
  }

  @Test
  public void testDeepCloneClonesAttributesMap() throws Exception {

    ActionCopy deepClonedJobEntry = (ActionCopy) originJobEntry.cloneDeep();
    assertNotNull( deepClonedJobEntry.getAttributesMap() );
    assertEquals( originJobEntry.getAttribute( ATTRIBUTE_GROUP, ATTRIBUTE_KEY ),
      deepClonedJobEntry.getAttribute( ATTRIBUTE_GROUP, ATTRIBUTE_KEY ) );
  }

}
