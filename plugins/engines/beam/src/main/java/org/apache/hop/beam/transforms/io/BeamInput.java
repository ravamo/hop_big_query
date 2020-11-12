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

package org.apache.hop.beam.transforms.io;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.ITransform;
import org.apache.hop.pipeline.transform.TransformMeta;

public class BeamInput extends BaseTransform<BeamInputMeta, BeamInputData> implements ITransform<BeamInputMeta, BeamInputData> {

  /**
   * This is the base transform that forms that basis for all transforms. You can derive from this class to implement your own
   * transforms.
   *
   * @param transformMeta          The TransformMeta object to run.
   * @param stepDataInterface the data object to store temporary data, database connections, caches, result sets,
   *                          hashtables etc.
   * @param copyNr            The copynumber for this transform.
   * @param pipelineMeta         The TransInfo of which the transform transformMeta is part of.
   * @param pipeline             The (running) transformation to obtain information shared among the transforms.
   */
  public BeamInput( TransformMeta transformMeta, BeamInputMeta meta, BeamInputData data, int copyNr, PipelineMeta pipelineMeta,
                    Pipeline pipeline ) {
    super( transformMeta, meta, data, copyNr, pipelineMeta, pipeline );
  }

  @Override public boolean processRow() throws HopException {


    // Outside of Beam this transform doesn't actually do anything, it's just metadata
    // This transform gets converted into Beam API calls in a pipeline
    //
    return false;
  }
}