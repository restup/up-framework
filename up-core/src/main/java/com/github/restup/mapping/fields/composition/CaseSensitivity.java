package com.github.restup.mapping.fields.composition;

import org.apache.commons.lang3.StringUtils;

import com.github.restup.annotations.field.CaseInsensitive;

public interface CaseSensitivity {

    boolean isCaseInsensitive();

    String getSearchField();

    boolean isLowerCased();

    static Builder builder() {
    		return new Builder();
    }

    static CaseSensitivity getCaseSensitivity(CaseInsensitive caseInsensitive) {
        return caseInsensitive == null ? null
                : builder()
                		.caseInsensitive(caseInsensitive.value())
                		.searchField(caseInsensitive.searchField())
                		.lowerCased(caseInsensitive.lowerCased())
                		.build();
    }

    static class Builder {
        private boolean caseInsensitive;
        private String searchField;
        private boolean lowerCased;
        
        private Builder() {
	        	caseInsensitive = true;
	        	lowerCased = true;
        }

        private Builder me() {
            return this;
        }

        public Builder caseInsensitive(boolean caseInsensitive) {
			this.caseInsensitive = caseInsensitive;
			return me();
		}

		public Builder searchField(String searchField) {
			this.searchField = StringUtils.isEmpty(searchField) ? null : searchField;
			return me();
		}

		public Builder lowerCased(boolean lowerCased) {
			this.lowerCased = lowerCased;
			return me();
		}

		public BasicCaseSensitivity build() {
            return new BasicCaseSensitivity(caseInsensitive, searchField, lowerCased);
        }
    }
    
}
