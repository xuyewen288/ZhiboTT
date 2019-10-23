package com.xunye.zhibott.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.shop.OperationCallback;
import com.mob.shop.ShopSDK;
import com.mob.shop.datatype.PriceSort;
import com.mob.shop.datatype.SalesSort;
import com.mob.shop.datatype.TimeSort;
import com.mob.shop.datatype.builder.ProductQuerier;
import com.mob.shop.datatype.entity.Label;
import com.mob.shop.datatype.entity.Product;
import com.xunye.zhibott.R;
import com.xunye.zhibott.acitvity.ModeChooseActivity;
import com.xunye.zhibott.adapter.CategoryAdapter;
import com.xunye.zhibott.adapter.SecondGoodsAdapter;
import com.xunye.zhibott.bean.Category;
import com.xunye.zhibott.bean.HotGoods;
import com.xunye.zhibott.bean.HttpContants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShopFragment} interface
 * to handle interaction events.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.recyclerview_category)
    RecyclerView          mRecyclerViewCategory;
    @BindView(R.id.recyclerview_wares)
    RecyclerView          mRecyclerviewWares;
    @BindView(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLaout;

    private Gson mGson         = new Gson();
    private List<Category> categoryFirst = new ArrayList<>();      //一级菜单
    private CategoryAdapter mCategoryAdapter;                      //一级菜单
    private SecondGoodsAdapter mSecondGoodsAdapter;              //二级菜单
    private List<HotGoods.ListBean> datas;

    private int mCategoryId;
    private int currPage  = 1;     //当前是第几页
    private int totalPage = 1;    //一共有多少页
    private int pageSize  = 10;     //每页数目

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final List<Long> list=new ArrayList<>();
        list.add(220221638354280448L);
        list.add(220208583189614592L);
        TimeSort timeSort=TimeSort.valueOf(0);
        PriceSort priceSort=PriceSort.valueOf(0);
        SalesSort salesSort=SalesSort.valueOf(0);
        ProductQuerier productQuerier=new ProductQuerier();
        productQuerier.pageIndex=1;
        productQuerier.pageSize=10;
        productQuerier.timeSort=timeSort;
        productQuerier.priceSort=priceSort;
        productQuerier.salesSort=salesSort;
        ShopSDK.getLabels(10,new OperationCallback<List<Label>>(){
            @Override
            public void onSuccess(List<Label> labels) {
                super.onSuccess(labels);
                Log.e("xyw","labels size=="+labels.size());
                if(labels.size()>0){
                    for (Label label:labels){
                        Log.e("xyw","label=="+label.getLabelName());
                    }
                }
            }
        });
//        ShopSDK.getProducts(list,timeSort,priceSort,salesSort,new OperationCallback<List<Product>>(){
        ShopSDK.getProducts(productQuerier,new OperationCallback<List<Product>>(){
            @Override
            public void onSuccess(List<Product> products) {
                super.onSuccess(products);
                Log.e("xyw","size=="+products.size());
                if(products.size()>0){
                    for (Product product:products){
                        Log.e("xyw","product=="+product.getProductName());
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                super.onFailed(throwable);
                Log.e("xyw","onFailed=="+throwable.getMessage());
                Log.e("xyw","onFailed=="+throwable.toString());
            }
        });

        requestCategoryData();
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                requestWares(mCategoryId);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void requestCategoryData() {

        OkHttpUtils.get().url(HttpContants.CATEGORY_LIST).build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("分类一级", e + "");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("分类一级", response + "");

                        Type collectionType = new TypeToken<Collection<Category>>() {
                        }.getType();
                        Collection<Category> enums = mGson.fromJson(response, collectionType);
                        Iterator<Category> iterator = enums.iterator();
                        while (iterator.hasNext()) {
                            Category bean = iterator.next();
                            categoryFirst.add(bean);
                        }

                        showCategoryData();
                        defaultClick();

                        if(categoryFirst.size()>0)
                            mCategoryId=categoryFirst.get(0).getId();

                    }
                });

    }

    /**
     * 展示一级菜单数据
     */
    private boolean isclick = false;
    private void showCategoryData() {

        mCategoryAdapter = new CategoryAdapter(categoryFirst);

        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Category category = (Category) adapter.getData().get(position);
                int id = category.getId();
                mCategoryId=id;
                String name = category.getName();
                isclick = true;
                defaultClick();
                requestWares(id);
            }
        });


        mRecyclerViewCategory.setAdapter(mCategoryAdapter);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewCategory.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewCategory.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

    }
    private void defaultClick() {

        //默认选中第0个
        if (!isclick) {
            Category category = categoryFirst.get(0);
            int id = category.getId();
            requestWares(id);
        }
    }

    /**
     * 二级菜单数据
     *
     * @param firstCategorId 一级菜单的firstCategorId
     */
    private void requestWares(int firstCategorId) {

        String url = HttpContants.WARES_LIST + "?categoryId=" + firstCategorId + "&curPage=" +
                currPage + "&pageSize=" + pageSize;

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                Log.e("二级菜单", e + "")
                ;
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("二级菜单", response + "");

                HotGoods hotGoods = mGson.fromJson(response, HotGoods.class);
                totalPage = hotGoods.getTotalPage();
                currPage = hotGoods.getCurrentPage();
                datas = hotGoods.getList();

                showData();
                mRefreshLaout.finishRefresh();

            }
        });
    }

    /**
     * 展示二级菜单的数据
     */
    private void showData() {
        switch (0) {
            case 0:

                mSecondGoodsAdapter = new SecondGoodsAdapter(getActivity(),datas);
                mSecondGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter
                        .OnItemClickListener() {

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HotGoods.ListBean listBean = (HotGoods.ListBean) adapter.getData().get
                                (position);

                        Intent intent = new Intent(getContext(), ModeChooseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("itemClickGoods", (Serializable) listBean);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });


                mRecyclerviewWares.setAdapter(mSecondGoodsAdapter);
                mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
                mRecyclerviewWares.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.HORIZONTAL));
                break;

            //            case STATE_REFREH:
            //                mAdatper.clearData();
            //                mAdatper.addData(datas);
            //                mRecyclerView.scrollToPosition(0);
            //                mRefreshLaout.finishRefresh();
            //                break;
            //
            //            case STATE_MORE:
            //                mAdatper.addData(mAdatper.getDatas().size(), datas);
            //                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
            //                mRefreshLaout.finishRefreshLoadMore();
            //                break;
        }
    }
}
