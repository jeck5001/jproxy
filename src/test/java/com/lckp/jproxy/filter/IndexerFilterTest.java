package com.lckp.jproxy.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.lckp.jproxy.filter.wrapper.RequestWrapper;
import com.lckp.jproxy.model.request.IndexerRequest;
import com.lckp.jproxy.service.IIndexerService;

class IndexerFilterTest {

	@Test
	void getIndexerRequestDefaultsOffsetAndLimitWhenProwlarrCapsOmitsPaging() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sonarr/prowlarr/2/api");
		request.addParameter("t", "caps");
		request.addParameter("apikey", "test");

		IndexerRequest indexerRequest = new TestIndexerFilter().getIndexerRequest(new RequestWrapper(request));

		assertThat(indexerRequest.getOffset()).isZero();
		assertThat(indexerRequest.getLimit()).isEqualTo(100);
	}

	@Test
	void updateRequestWrapperUsesKeywordSearchForTvSearch() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/sonarr/prowlarr/2/api");
		request.addParameter("t", "tvsearch");
		request.addParameter("q", "Swallowed Star");
		request.addParameter("season", "5");
		request.addParameter("ep", "200");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		IndexerRequest indexerRequest = new TestIndexerFilter().getIndexerRequest(requestWrapper);
		indexerRequest.setSearchType("search");
		indexerRequest.setSeasonNumber("");
		indexerRequest.setEpisodeNumber("");
		new TestIndexerFilter().updateRequestWrapper(indexerRequest, requestWrapper);

		assertThat(requestWrapper.getParameter("t")).isEqualTo("search");
		assertThat(requestWrapper.getParameter("q")).isEqualTo("Swallowed Star");
		assertThat(requestWrapper.getParameter("season")).isEmpty();
		assertThat(requestWrapper.getParameter("ep")).isEmpty();
	}

	private static class TestIndexerFilter extends IndexerFilter {

		TestIndexerFilter() {
			super(new NoopIndexerService());
		}
	}

	private static class NoopIndexerService implements IIndexerService {
		@Override
		public String getTitle(String searchKey) {
			return searchKey;
		}

		@Override
		public List<String> getSearchTitle(String title) {
			return List.of(title);
		}

		@Override
		public String generateCacheKey(RequestWrapper requestWrapper) {
			return "";
		}

		@Override
		public String generateOffsetKey(RequestWrapper requestWrapper) {
			return "";
		}

		@Override
		public int calculateCurrentIndex(int offset, List<Integer> offsetList) {
			return 0;
		}

		@Override
		public List<Integer> getOffsetList(String key, int size) {
			return List.of();
		}

		@Override
		public List<Integer> updateOffsetList(String key, List<Integer> offsetList) {
			return offsetList;
		}

		@Override
		public void updateIndexerRequest(int index, List<String> searchTitleList, List<Integer> offsetList,
				IndexerRequest indexerRequest) {
		}

		@Override
		public String executeNewRequest(RequestWrapper requestWrapper) {
			return "";
		}

		@Override
		public String executeFormatRule(String xml) {
			return xml;
		}

		@Override
		public int getMinCount() {
			return -1;
		}
	}
}
