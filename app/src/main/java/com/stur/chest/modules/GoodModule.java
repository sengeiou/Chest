package com.stur.chest.modules;

import com.stur.chest.utils.ApiUtils;
import com.stur.lib.web.HttpFactory;
import com.stur.lib.web.base.HttpListener;
import com.stur.lib.web.request.HttpRequest;


public class GoodModule {
    public static void getCategoryList(HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getCategoryListApi();
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void getGoodsByCategoryId(int categoryId, int page, int size, HttpListener httpListener) {
        getGoodList(null, null, categoryId, page, size, httpListener);
    }

    public static void searchGoodList(String name, int page, int size, HttpListener httpListener) {
        getGoodList(null, name, 0, page, size, httpListener);
    }

    private static void getGoodList(String category, String name, int categoryId,
                                    int page, int size, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getGoodListApi();
        if (category != null) {
            httpRequest.addParams(ApiUtils.KEY_CATEGORY, category);
        }
        if (name != null) {
            httpRequest.addParams(ApiUtils.KEY_NAME, name);
        }
        if (categoryId > 0) {
            httpRequest.addParams(ApiUtils.KEY_CATEGORY_ID, String.valueOf(categoryId));
        }
        if ((page > 0) && (size > 0)) {
            httpRequest.addParams(ApiUtils.KEY_PAGE, String.valueOf(page));
            httpRequest.addParams(ApiUtils.KEY_SIZE, String.valueOf(size));
        }
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }

    public static void getGoodDetail(int goodsId, HttpListener httpListener) {
        HttpRequest httpRequest = ApiUtils.getGoodDetailApi();
        httpRequest.addParams(ApiUtils.KEY_GOODS_ID, String.valueOf(goodsId));
        HttpFactory.getHttpService().sendRequest(httpRequest, httpListener);
    }
}
