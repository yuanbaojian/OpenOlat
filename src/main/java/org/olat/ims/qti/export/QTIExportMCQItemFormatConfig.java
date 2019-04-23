/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/

package org.olat.ims.qti.export;

public class QTIExportMCQItemFormatConfig implements QTIExportItemFormatConfig {
	private boolean responseCols;
	private boolean positionsOfResponsesCol;
	private boolean pointCol;
	private boolean timeCols;
	
	public QTIExportMCQItemFormatConfig(boolean resCols, boolean posOfResCol, boolean pointCol, boolean timeCols){
		this.responseCols = resCols;
		this.positionsOfResponsesCol = posOfResCol;
		this.pointCol = pointCol;
		this.timeCols = timeCols;
	}

	public boolean hasResponseCols() {
		return responseCols;
	}

	public boolean hasPositionsOfResponsesCol() {
		return positionsOfResponsesCol;
	}

	public boolean hasPointCol() {
		return pointCol;
	}

	public boolean hasTimeCols() {
		return timeCols;
	}

	public void setPointCol(boolean pointColConfigured) {
		this.pointCol = pointColConfigured;
	}

	public void setPositionsOfResponsesCol(boolean positionsOfResponsesColConfigured) {
		this.positionsOfResponsesCol = positionsOfResponsesColConfigured;
	}

	public void setResponseCols(boolean responseColsConfigured) {
		this.responseCols = responseColsConfigured;
	}

	public void setTimeCols(boolean timeColsConfigured) {
		this.timeCols = timeColsConfigured;
	}

}
