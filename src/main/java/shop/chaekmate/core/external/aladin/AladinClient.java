package shop.chaekmate.core.external.aladin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.external.aladin.dto.response.AladinApiResponse;

@FeignClient(
        name = "aladin-api",
        url = "${aladin.api.base-url}"
)
public interface AladinClient {

    @GetMapping("/ItemSearch.aspx")
    AladinApiResponse searchBooks(
            @RequestParam("ttbkey") String ttbkey,
            @RequestParam("Query") String query,
            @RequestParam("QueryType") String queryType,
            @RequestParam("MaxResults") Integer maxResults,
            @RequestParam("start") Integer start,
            @RequestParam("output") String output,
            @RequestParam("Version") String version
    );
}
