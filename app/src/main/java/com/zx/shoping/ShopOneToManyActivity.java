package com.zx.shoping;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zx.shoping.bean.shopmany.Goods;
import com.zx.shoping.bean.shopmany.Shops;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.loadingviewfinal.HeaderAndFooterRecyclerViewAdapter;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;

public class ShopOneToManyActivity  extends AppCompatActivity {

    @Bind(R.id.rv_list)
    RecyclerViewFinal mRvList;
    @Bind(R.id.cb_select_all)
    CheckBox cbSelectAll;
    @Bind(R.id.tv_delete_num)
    TextView tvDeleteNum;
    @Bind(R.id.tv_remain_num)
    TextView tvRemainNum;
    @Bind(R.id.tv_delete)
    Button tvDelete;
    private CommonAdapter mCommonAdapter;
    private List<Shops> parentShopings = new ArrayList<>();
    protected static final int SELECT_TRUE = 1, SELECT_FALSE = 0;
    private int allNum;
    private int deleteNum;
    String[] parents = {"森马官方店", "李宁旗舰店", "安踏旗舰店"};
    String[] childs1 = {"森马男外套", "森马裤子", "森马T桖"};
    String[] childs2 = {"李宁运动外套", "李宁男鞋", "李宁跑步"};
    String[] childs3 = {"安踏运动套装", "安踏帽子", "安踏运动鞋"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    public Context getContext() {
        return this;
    }

    private void initData() {
        initDatas();
        initNum();
    }

    private void initDatas() {
        List<String[]> listArray = new ArrayList<>();
        listArray.add(childs1);
        listArray.add(childs2);
        listArray.add(childs3);
        for (int i = 0; i < listArray.size(); i++) {
            Shops shops = new Shops();
            shops.setName(parents[i]);
            shops.setGoodsList(getChilds(listArray.get(i)));
            parentShopings.add(shops);
        }
    }

    private List<Goods> getChilds(String[] arrays) {
        List<Goods> list = new ArrayList<>();
        for (int j = 0; j < arrays.length; j++) {
            Goods goods = new Goods();
            goods.setName(arrays[j]);
            list.add(goods);
        }
        return list;
    }


    private void initNum() {
        for (int i = 0; i < parentShopings.size(); i++) {
            allNum = allNum + parentShopings.get(i).getGoodsList().size();
        }
        setNum();
    }

    private void initView() {
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommonAdapter = new CommonAdapter<Shops>(getContext(), R.layout.listitem_shop, parentShopings) {
            @Override
            public void convert(ViewHolder holder, Shops shop, int position) {
                final int parentPosition = position;
                holder.setText(R.id.tv_name, shop.getName());
                final CheckBox cbSelect = holder.getView(R.id.cb_select);
                cbSelect.setChecked(shop.getIsSelect() == SELECT_TRUE ? true : false);

                RecyclerViewFinal rvList = holder.getView(R.id.rv_list);
                rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                final List<Goods> goods = shop.getGoodsList();
                final CommonAdapter adapter = new CommonAdapter<Goods>(getContext(), R.layout.listitem_shop_child, goods) {
                    @Override
                    public void convert(ViewHolder holder, Goods goods, int position) {
                        holder.setText(R.id.tv_name, goods.getName());
                        holder.setChecked(R.id.cb_select, goods.getIsSelect() == SELECT_TRUE ? true : false);
                    }
                };
                rvList.setAdapter(adapter);
                rvList.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                        changeChildSelectStatus(goods, adapter, cbSelect, parentPosition, position);
                    }
                });
            }
        };

        mRvList.setAdapter(mCommonAdapter);
        mRvList.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                changeSelectStatus(position);

            }
        });

    }

    /**
     * 更改当前Item选中的状态
     *
     * @param position
     */
    protected void changeSelectStatus(int position) {
        Shops shops = parentShopings.get(position);
        if (shops.getIsSelect() == SELECT_TRUE) {
            shops.setIsSelect(SELECT_FALSE);
            resetChildSelectAllItemStatusArray(SELECT_FALSE, shops.getGoodsList());

        } else {
            shops.setIsSelect(SELECT_TRUE);
            resetChildSelectAllItemStatusArray(SELECT_TRUE, shops.getGoodsList());
        }
        //父集全选
        if (isSelectAll()) {
            cbSelectAll.setChecked(true);
        } else {
            cbSelectAll.setChecked(false);
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 更改当前子集Item选中的状态
     *
     * @param
     */
    protected void changeChildSelectStatus(List<Goods> currentGoods, CommonAdapter adapter, CheckBox cbSelect, int parentPosition, int position) {
        if (currentGoods.get(position).getIsSelect() == SELECT_TRUE) {
            currentGoods.get(position).setIsSelect(SELECT_FALSE);
        } else {
            currentGoods.get(position).setIsSelect(SELECT_TRUE);
        }
        adapter.notifyDataSetChanged();
        if (childIsSelectAll(currentGoods)) {
            parentShopings.get(parentPosition).setIsSelect(SELECT_TRUE);
            cbSelect.setChecked(true);
        } else {
            parentShopings.get(parentPosition).setIsSelect(SELECT_FALSE);
            cbSelect.setChecked(false);
        }
        //父集全选
        if (isSelectAll()) {
            cbSelectAll.setChecked(true);
        } else {
            cbSelectAll.setChecked(false);
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 判断是否全选
     *
     * @return
     */
    boolean isSelectAll() {
        for (int i = 0; i < parentShopings.size(); i++) {
            if (parentShopings.get(i).getIsSelect() == SELECT_FALSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断子集是否全选
     *
     * @return
     */
    boolean childIsSelectAll(List<Goods> currentGoods) {
        for (int i = 0; i < currentGoods.size(); i++) {
            if (currentGoods.get(i).getIsSelect() == SELECT_FALSE) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全选按钮
     *
     * @param v
     */
    @OnClick(R.id.cb_select_all)
    void selectAllOnClick(View v) {
        if (isSelectAll()) {
            resetSelectAllItemStatusArray(SELECT_FALSE);
            cbSelectAll.setChecked(false);
        } else {
            resetSelectAllItemStatusArray(SELECT_TRUE);
            cbSelectAll.setChecked(true);
        }
    }

    @OnClick(R.id.tv_delete)
    void delete(View v) {
        deleteCodes();
    }

    /**
     * 删除当前选中的所有的码（手动）
     */

    void deleteCodes() {
        if (parentShopings == null || parentShopings.size() == 0) {
            Toast.makeText(getContext(), "没有数据可以删除", Toast.LENGTH_SHORT);
            return;
        }
        List<Shops> parentList = new ArrayList<>();
        for (int i = 0; i < parentShopings.size(); i++) {
            if (parentShopings.get(i).getIsSelect() == SELECT_TRUE) {
                parentList.add(parentShopings.get(i));
                deleteNum = deleteNum + parentShopings.get(i).getGoodsList().size();
            } else {
                List<Goods> goods = parentShopings.get(i).getGoodsList();
                List<Goods> list = new ArrayList<>();
                for (int j = 0; j < goods.size(); j++) {
                    if (goods.get(j).getIsSelect() == SELECT_TRUE) {
                        list.add(goods.get(j));
                        deleteNum = deleteNum + 1;
                    }
                }
                goods.removeAll(list);
            }
        }
        parentShopings.removeAll(parentList);
        mCommonAdapter.notifyDataSetChanged();
        setNum();
    }

    private void setNum() {
        tvDeleteNum.setText("已删：" + String.valueOf(deleteNum));
        tvRemainNum.setText("还剩：" + String.valueOf(allNum - deleteNum));
    }

    /**
     * 全部选中logisticCodes的所有值
     * 全选中 arrValue = SELECT_TRUE
     * 全不选 arrValue = SELECT_FALSE
     */
    void resetSelectAllItemStatusArray(int arrValue) {
        for (int i = 0; i < parentShopings.size(); i++) {
            parentShopings.get(i).setIsSelect(arrValue);
            for (int j = 0; j < parentShopings.get(i).getGoodsList().size(); j++) {
                parentShopings.get(i).getGoodsList().get(j).setIsSelect(arrValue);
            }
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    /**
     * 全部选中logisticCodes的所有值
     * 全选中 arrValue = SELECT_TRUE
     * 全不选 arrValue = SELECT_FALSE
     */
    void resetChildSelectAllItemStatusArray(int arrValue, List<Goods> goods) {
        for (int i = 0; i < goods.size(); i++) {
            goods.get(i).setIsSelect(arrValue);
        }
        mCommonAdapter.notifyDataSetChanged();
    }
}
