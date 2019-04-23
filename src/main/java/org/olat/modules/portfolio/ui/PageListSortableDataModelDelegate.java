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
package org.olat.modules.portfolio.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.olat.core.commons.persistence.SortKey;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableModelDelegate;
import org.olat.modules.portfolio.Page;
import org.olat.modules.portfolio.PageStatus;
import org.olat.modules.portfolio.Section;
import org.olat.modules.portfolio.ui.PageListDataModel.PageCols;
import org.olat.modules.portfolio.ui.model.PortfolioElementRow;

/**
 * 
 * Initial date: 23.06.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PageListSortableDataModelDelegate extends SortableFlexiTableModelDelegate<PortfolioElementRow> {
	
	private final boolean flat;
	
	public PageListSortableDataModelDelegate(SortKey orderBy, PageListDataModel tableModel, boolean flat, Locale locale) {
		super(orderBy, tableModel, locale);
		this.flat = flat;
	}

	@Override
	protected void sort(List<PortfolioElementRow> rows) {
		Comparator<PortfolioElementRow> comparator = new PageCreationDateComparator();
		if(getOrderBy() != null) {
			int columnIndex = getColumnIndex();
			PageCols column = PageCols.values()[columnIndex];
			switch(column) {
				case key: comparator = new PageCreationDateComparator(); break;
				case status: comparator = new StatusComparator(); break;
				case comment: comparator = new CommentsComparator(); break;
				default: comparator = new DefaultComparator(); break;
			}
		}
		if(!isAsc()) {
			comparator = new ReverseComparator(comparator);
		}
		
		if(flat) {
			Collections.sort(rows, comparator);
		} else {
			Collections.sort(rows, new ClassicComparator(comparator));
		}
	}

	@Override
	protected void reverse(List<PortfolioElementRow> rows) {
		//do nothing
	}
	
	private static class ReverseComparator implements Comparator<PortfolioElementRow> {
		
		private Comparator<PortfolioElementRow> delegate;
		
		public ReverseComparator(Comparator<PortfolioElementRow> delegate) {
			this.delegate = delegate;
		}

		@Override
		public int compare(PortfolioElementRow o1, PortfolioElementRow o2) {
			return -delegate.compare(o1, o2);
		}
	}
	
	private final class CommentsComparator implements Comparator<PortfolioElementRow> {
		@Override
		public int compare(PortfolioElementRow o1, PortfolioElementRow o2) {
			long c1 = o1.getCommentFormLink() == null ? 0 : o1.getNumOfComments();
			long c2 = o2.getCommentFormLink() == null ? 0 : o2.getNumOfComments();
			int c = Long.compare(c1, c2);
			if(c == 0) {
				c = Boolean.compare(o1.getCommentFormLink() != null, o2.getCommentFormLink() != null);
			}
			if(c == 0) {
				c = compareString(o1.getTitle(), o2.getTitle());
			}
			return c;
		}
	}
	
	private final class StatusComparator extends PageComparator {
		@Override
		public int compare(Page p1, Page p2) {
			PageStatus s1 = p1.getPageStatus();
			PageStatus s2 = p2.getPageStatus();
			if(s1 == null && s2 != null) {
				return -1;
			}
			if(s1 != null && s2 == null) {
				return 1;
			}
			
			int compare = 0;
			if(s1 != null && s2 != null) {
				compare = Integer.compare(s1.ordinal(), s2.ordinal());
			}
			return compare;
		}
	}
	
	private final class PageCreationDateComparator extends PageComparator {
		@Override
		public int compare(Page p1, Page p2) {
			return compareDateAndTimestamps(p1.getCreationDate(), p2.getCreationDate());
		}
	}
	
	public abstract class PageComparator implements Comparator<PortfolioElementRow> {
		
		@Override
		public int compare(PortfolioElementRow t1, PortfolioElementRow t2) {
			Page p1 = t1.getPage();
			Page p2 = t2.getPage();
			if(p1 == null && p2 != null) {
				return 1;
			}
			if(p1 != null && p2 == null) {
				return -1;
			}
			
			int c = 0;
			if(p1 != null && p1 != null) {
				c = compare(p1, p2);
			}
			if(c == 0) {
				c = compareString(t1.getTitle(), t2.getTitle());
			}
			return c;
		}
		
		protected abstract int compare(Page p1, Page p2);
	}

	private final class ClassicComparator implements Comparator<PortfolioElementRow> {
		
		private final Comparator<PortfolioElementRow> lastComparator;
		
		public ClassicComparator(Comparator<PortfolioElementRow> lastComparator) {
			this.lastComparator = lastComparator;
		}
		
		@Override
		public int compare(PortfolioElementRow r1, PortfolioElementRow r2) {
			if(r1.getNewFloatingEntryLink() != null) {
				return -1;
			}
			if(r2.getNewFloatingEntryLink() != null) {
				return 1;
			}

			Section s1 = r1.getSection();
			Section s2 = r2.getSection();
			int c = compareSections(s1, s2);
			if(c == 0) {
				boolean a1 = r1.isPendingAssignment();
				boolean a2 = r2.isPendingAssignment();
				if(a1 && a2) {
					int apc1 = r1.getAssignmentPos();
					int apc2 = r2.getAssignmentPos();
					c = compareInts(apc1, apc2);
				} else if(a1) {
					if(r2.isSection()) {
						c = 1;
					} else if(r2.isPage()) {
						c = -1;
					}
				} else if(a2) {
					if(r1.isSection()) {
						c = -1;
					} else if(r1.isPage()) {
						c = 1;
					}
				} else {
					boolean p1 = r1.isPage();
					boolean p2 = r2.isPage();
					if(p1 && p2) {
						c = lastComparator.compare(r1, r2);
					} else if(p1) {
						if(r2.isSection()) {
							c = 1;
						}
					} else if(p2) {
						if(r1.isSection()) {
							c = -1;
						}
					}
				}
			}
			return c;
		}
		
		private int compareSections(Section s1, Section s2) {
			if(s1 == null && s2 == null) {
				return 0;
			}
			if(s1 == null && s2 != null) {
				return 1;
			}
			if(s1 != null && s2 == null) {
				return -1;
			}
			if(s1.equals(s2)) {
				return 0;
			}
			
			Date b1 = s1.getBeginDate();
			if(b1 == null) {
				b1 = s1.getCreationDate();
			}
			Date b2 = s2.getBeginDate();
			if(b2 == null) {
				b2 = s2.getCreationDate();
			}
			
			int c = compareDateAndTimestamps(b1, b2);
			if(c == 0) {
				c = compareLongs(s1.getKey(), s2.getKey());
			}
			return c;
		}
	}
}