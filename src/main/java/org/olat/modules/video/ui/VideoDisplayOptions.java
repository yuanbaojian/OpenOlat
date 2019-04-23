/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.video.ui;

/**
 * 
 * 
 * Initial date: 6 déc. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class VideoDisplayOptions {
	
	private boolean autoplay;
	private boolean showComments;
	private boolean showRating;
	private boolean showTitleAndDescription;
	private boolean customDescription;
	private boolean autoWidth;
	private boolean readOnly;
	private String descriptionText;
	private boolean showAnnotations;
	private boolean showQuestions;
	private boolean showPoster;
	private boolean alwaysShowControls;
	private boolean dragAnnotations;
	private boolean clickToPlayPause;
	private boolean authorMode;
	
	public static VideoDisplayOptions valueOf(boolean autoplay, boolean showComments, boolean showRating, boolean showTitleAndDescription,
			boolean customDescription, boolean autoWidth, String descriptionText, boolean authorMode, boolean readOnly) {
		VideoDisplayOptions options = new VideoDisplayOptions();
		options.setAutoplay(autoplay);
		options.setAutoWidth(autoWidth);
		options.setCustomDescription(customDescription);
		options.setDescriptionText(descriptionText);
		options.setReadOnly(readOnly);
		options.setShowComments(showComments);
		options.setShowRating(showRating);
		options.setShowTitleAndDescription(showTitleAndDescription);
		options.setShowPoster(true);
		options.setShowQuestions(true);
		options.setShowAnnotations(true);
		options.setAlwaysShowControls(false);
		options.setDragAnnotations(false);
		options.setClickToPlayPause(true);
		options.setAuthorMode(authorMode);
		return options;
	}
	
	public static VideoDisplayOptions disabled() {
		VideoDisplayOptions options = new VideoDisplayOptions();
		options.setAutoplay(false);
		options.setAutoWidth(false);
		options.setCustomDescription(false);
		options.setDescriptionText(null);
		options.setReadOnly(false);
		options.setShowComments(false);
		options.setShowRating(false);
		options.setShowTitleAndDescription(false);
		options.setShowPoster(true);
		options.setShowQuestions(false);
		options.setShowAnnotations(false);
		options.setAlwaysShowControls(false);
		options.setDragAnnotations(false);
		options.setClickToPlayPause(true);
		options.setAuthorMode(false);
		return options;
	}
	
	public boolean isAutoplay() {
		return autoplay;
	}
	
	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}
	
	public boolean isAlwaysShowControls() {
		return alwaysShowControls;
	}

	public void setAlwaysShowControls(boolean alwaysShowControls) {
		this.alwaysShowControls = alwaysShowControls;
	}

	public boolean isClickToPlayPause() {
		return clickToPlayPause;
	}

	public void setClickToPlayPause(boolean clickToPlayPause) {
		this.clickToPlayPause = clickToPlayPause;
	}

	public boolean isShowComments() {
		return showComments;
	}
	
	public void setShowComments(boolean showComments) {
		this.showComments = showComments;
	}
	
	public boolean isShowRating() {
		return showRating;
	}
	
	public void setShowRating(boolean showRating) {
		this.showRating = showRating;
	}
	
	public boolean isShowAnnotations() {
		return showAnnotations;
	}

	public void setShowAnnotations(boolean showAnnotations) {
		this.showAnnotations = showAnnotations;
	}
	
	public boolean isDragAnnotations() {
		return dragAnnotations;
	}

	public void setDragAnnotations(boolean dragAnnotations) {
		this.dragAnnotations = dragAnnotations;
	}

	public boolean isShowQuestions() {
		return showQuestions;
	}

	public void setShowQuestions(boolean showQuestions) {
		this.showQuestions = showQuestions;
	}

	public boolean isShowTitleAndDescription() {
		return showTitleAndDescription;
	}
	
	public void setShowTitleAndDescription(boolean showTitleAndDescription) {
		this.showTitleAndDescription = showTitleAndDescription;
	}
	
	public boolean isCustomDescription() {
		return customDescription;
	}
	
	public void setCustomDescription(boolean customDescription) {
		this.customDescription = customDescription;
	}
	
	public boolean isAutoWidth() {
		return autoWidth;
	}
	
	public void setAutoWidth(boolean autoWidth) {
		this.autoWidth = autoWidth;
	}
	
	public boolean isShowPoster() {
		return showPoster;
	}

	public void setShowPoster(boolean showPoster) {
		this.showPoster = showPoster;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public String getDescriptionText() {
		return descriptionText;
	}
	
	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}

	public boolean isAuthorMode() {
		return authorMode;
	}

	public void setAuthorMode(boolean authorMode) {
		this.authorMode = authorMode;
	}
}
