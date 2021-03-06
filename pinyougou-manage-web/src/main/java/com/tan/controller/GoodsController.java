package com.tan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tan.pojo.TbGoods;
import com.tan.pojo.TbItem;
import com.tan.search.service.ItemSearchService;
import com.tan.sellergoods.service.GoodsService;
import com.tan.vo.Goods;
import com.tan.vo.PageResult;
import com.tan.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {

        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            goods.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            goods.getGoods().setAuditStatus("0");
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 删除分2种，一种物理删除，一种逻辑删除
     * @param ids
     * @return
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {

        return goodsService.search(page, rows, goods);
    }

    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids ,String status){
        try {
            goodsService.updateStatus(ids,status);

            if("2".equals(status)){
                List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids, status);
                itemSearchService.ImportItemList(itemList);
            }
            return Result.ok("审核通过！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("审核失败！");
    }

    /*   @GetMapping(value = {"goodsid"})
    *//*@RequestMapping(value = {"id"},method = RequestMethod.GET)*//*
    public String findGoodsAndItemCat(@PathVariable Integer goodsid){
       Map<String,Object>  map1= itemService.seleteGoodsAndItemCat(goodsid);
      return  goodsid.toString();
    }*/

}
