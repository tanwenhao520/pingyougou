package com.tan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.dao.*;
import com.tan.pojo.*;
import com.tan.sellergoods.service.GoodsService;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
@Transactional
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private  ItemCatMapper itemCatMapper;

    @Autowired
    private SellerMapper  sellerMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

            criteria.andNotEqualTo("isDelete","1");

        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId", goods.getSellerId() );
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus", goods.getAuditStatus() );
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%"+goods.getGoodsName()+"%" );
        }
        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }



    @Override
    public void add(Goods goods) {
        goodsMapper.insertSelective(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
    }

    @Override
    public void addGoods(Goods goods) {
        goodsMapper.insertSelective(goods.getGoods());

        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());

        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        saveItem(goods);

        }

    @Override
    public void updateStatus(Long[] ids,String status) {
        if(ids.length > 0 ){

            TbGoods tg = new TbGoods();
            tg.setAuditStatus(status);

            Example example = new Example(TbGoods.class);

            example.createCriteria().andIn("id", Arrays.asList(ids));

            goodsMapper.updateByExampleSelective(tg,example);

            if("2".equals(status)){
                TbItem tbItem = new TbItem();
                tbItem.setStatus("1");
                Example example1 = new Example(TbItem.class);
                example1.createCriteria().andIn("goodsId",Arrays.asList(ids));
                itemMapper.updateByExampleSelective(tbItem,example1);
            }
        }

    }

    @Override
    public Goods findGoodsById(Long id) {

        Goods goods = new Goods();

        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

        goods.setGoods(tbGoods);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goods.getGoods().getId());

        goods.setGoodsDesc(tbGoodsDesc);

        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",goods.getGoodsDesc().getGoodsId());
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        goods.setItemList(tbItems);

        return goods;
    }

    @Override
    public void update(Goods goods) {

        goods.getGoods().setAuditStatus("0");

        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        TbItem tb = new TbItem();

        tb.setGoodsId(goods.getGoodsDesc().getGoodsId());

        itemMapper.delete(tb);

        saveItem(goods);
    }

    @Override
    public void deleteGoodsByIds(String[] ids) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);


    }

    private void  saveItem(Goods goods){
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();
                Map<String,Object> map = JSON.parseObject(tbItem.getSpec());

                Set<Map.Entry<String, Object>> entries = map.entrySet();

                for (Map.Entry<String, Object> entry : entries) {
                    title += " "+entry.getValue();
                }

                tbItem.setTitle(title);

                setItemValue(tbItem,goods);

                itemMapper.insert(tbItem);
             }

        }else{
            TbItem tb = new TbItem();
            tb.setTitle(goods.getGoods().getGoodsName());
            tb.setPrice(goods.getGoods().getPrice());
            tb.setNum(9999);
            tb.setStatus("0");
            tb.setIsDefault("1");
            tb.setSpec("{}");

            setItemValue(tb,goods);

            itemMapper.insert(tb);
        }


    }


    private void  setItemValue(TbItem tbItem,Goods goods){

        List<Map>  arr = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);

        if(arr != null && arr.size() > 0){
            tbItem.setImage(arr.get(0).get("url").toString());
        }

        tbItem.setCategoryid(goods.getGoods().getCategory3Id());

        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());


        tbItem.setCategory(tbItemCat.getName());

        tbItem.setCreateTime(new Date());

        tbItem.setUpdateTime(tbItem.getCreateTime());

        tbItem.setSellerId(goods.getGoods().getSellerId());

        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

        tbItem.setSeller(seller.getName());

        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());

        tbItem.setBrand(tbBrand.getName());
    }

}
