package com.stur.chest.control;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.stur.chest.db.CategoryListProxy;
import com.stur.chest.db.CategoryProvider;
import com.stur.chest.db.SharedPrefsHelper;
import com.stur.chest.dto.AbstractProductDTO;
import com.stur.chest.dto.AddressDTO;
import com.stur.chest.dto.ApiDTO;
import com.stur.chest.dto.ApiListDTO;
import com.stur.chest.dto.CategoryDTO;
import com.stur.chest.dto.MainCategory;
import com.stur.chest.dto.ProductDTO;
import com.stur.chest.modules.AddressModule;
import com.stur.chest.modules.GoodModule;
import com.stur.chest.modules.OrderModule;
import com.stur.lib.Log;
import com.stur.lib.exception.AppOperactionException;
import com.stur.lib.exception.UnLoginException;
import com.stur.lib.web.api.HttpResponseListener;
import com.stur.lib.web.response.HttpError;
import com.stur.lib.web.response.HttpResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxuejin on 2018/3/2.
 */

public class ChestController {
    private static ChestController sInstance;
    private CategoryProvider categoryProvider;
    private CategoryListProxy categoryListProxy;

    private ChestController() {
        categoryListProxy = new CategoryListProxy();
    }

    public static ChestController getInstance() {
        if (sInstance == null) {
            sInstance = new ChestController();
        }
        return sInstance;
    }

    public void init(Context context) {
        categoryProvider = new CategoryProvider(context);
    }

    public CategoryListProxy getCategoryListProxy() {
        return categoryListProxy;
    }

    public void getAllCategories() {
        getAllCategories(null);
    }

    public void getAllCategories(final OnHttpResponseParsedListener listener) {
        GoodModule.getCategoryList(new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                try {
                    Log.d(this, "[response]" + response.toString());
                    Type type = new TypeToken<ApiDTO<ArrayList<CategoryDTO>>>() {
                    }.getType();
                    ApiDTO<List<CategoryDTO>> apiDTO = response.convert(type);
                    List<CategoryDTO> dto = apiDTO.getData();
                    categoryProvider.deleteAllCategories();
                    for (CategoryDTO categoryDTO : dto) {
                        Log.d(this, "category: " + categoryDTO.toString());
                        categoryProvider.insertProductInfo(categoryDTO);
                    }
                    initCategoryList();
                    if (listener != null) {
                        listener.onFinish(null);
                    }
                } catch (AppOperactionException e) {
                    e.printStackTrace();
                } catch (UnLoginException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initCategoryList() {
        List<CategoryDTO> mainCategories = categoryProvider.getProductInfoListByParentId(0);
        for (CategoryDTO categoryDTO : mainCategories) {
            MainCategory mainCategory = new MainCategory(categoryDTO.getId(), categoryDTO.getName(),
                    categoryDTO.getImg());
            List<CategoryDTO> subCategories = categoryProvider.getProductInfoListByParentId(categoryDTO.getId());
            for (CategoryDTO subCategory : subCategories) {
                mainCategory.addSubCategory(subCategory);
            }
            categoryListProxy.addMainCategory(mainCategory);
        }
    }

    public void getGoodsByCategoryId(int categoryId, int page, int size,
                                     final OnHttpResponseParsedListener<List<AbstractProductDTO>> listener) {
        GoodModule.getGoodsByCategoryId(categoryId, page, size, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[getGoodsByCategoryId]" + response.toString());
                Type type = new TypeToken<ApiListDTO<AbstractProductDTO>>() {
                }.getType();
                ApiListDTO<AbstractProductDTO> dto = response.convert(type);

                List<AbstractProductDTO> results = null;
                try {
                    results = dto.getData();
                    listener.onFinish(results);
                } catch (AppOperactionException e) {
                    e.printStackTrace();
                } catch (UnLoginException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void searchGoodByName(String name, int page, int size,
                                 final OnHttpResponseParsedListener<List<AbstractProductDTO>> listener) {
        GoodModule.searchGoodList(name, page, size, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[searchGoodByName]" + response.toString());
                Type type = new TypeToken<ApiListDTO<AbstractProductDTO>>() {
                }.getType();
                ApiListDTO<AbstractProductDTO> dto = response.convert(type);

                try {
                    List<AbstractProductDTO> results = dto.getData();
                    listener.onFinish(results);
                } catch (AppOperactionException e) {
                    e.printStackTrace();
                } catch (UnLoginException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getProductDetailById(int productId, final OnHttpResponseParsedListener<ProductDTO> onHttpResponseParsedListener) {
        GoodModule.getGoodDetail(productId, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[getProductDetailById]" + response.toString());
                Type type = new TypeToken<ApiDTO<ProductDTO>>() {
                }.getType();
                ApiDTO<ProductDTO> dto = response.convert(type);
                ProductDTO productDTO = null;
                try {
                    productDTO = dto.getData();
                    onHttpResponseParsedListener.onFinish(productDTO);
                } catch (AppOperactionException e) {
                    e.printStackTrace();
                } catch (UnLoginException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpError error) {
                super.onFailure(error);
                Log.e(this, error.toString());
            }
        });
    }

    public void getAddressList(final OnHttpResponseParsedListener<List<AddressDTO>> onHttpResponseParsedListener) {
        AddressModule.getAddressList(SharedPrefsHelper.getToken(), new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d(this, "[response]" + response.toString());
                Type type = new TypeToken<ApiListDTO<AddressDTO>>() {
                }.getType();
                ApiListDTO<AddressDTO> apiDTO = response.convert(type);
                try {
                    List<AddressDTO> dataList = apiDTO.getData();
                    onHttpResponseParsedListener.onFinish(dataList);
                } catch (AppOperactionException e) {
                    e.printStackTrace();
                } catch (UnLoginException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void removeAddress(int id, final OnHttpResponseParsedListener<String> onHttpResponseParsedListener) {
        AddressModule.deleteAddress(SharedPrefsHelper.getToken(), id, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                onHttpResponseParsedListener.onFinish(response.toString());
            }
        });
    }

    public void confirmOrder(int addrId, int itemId, int num, final OnHttpResponseParsedListener<String> onHttpResponseParsedListener) {
        OrderModule.createOrder(SharedPrefsHelper.getToken(), addrId, itemId, num, new HttpResponseListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                onHttpResponseParsedListener.onFinish(response.toString());
            }
        });
    }

    public interface OnHttpResponseParsedListener<T> {
        void onFinish(T obj);
    }
}
