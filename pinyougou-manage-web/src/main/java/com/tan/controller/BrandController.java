package com.tan.controller;

        import com.alibaba.dubbo.config.annotation.Reference;
        import com.tan.pojo.TbBrand;
        import com.tan.sellergoods.service.BrandService;
        import com.tan.vo.PageResult;
        import com.tan.vo.Result;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;
        import java.util.Map;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;


   /* @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                  @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return (List<TbBrand>) brandService.findPage(page,rows).getRows();
    }*/
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return brandService.findPage(page,rows);
    }
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 新增品牌
     * @param tbBrand
     * @return 新增成功则返回Rusult结果(里面封装着返回的信息)
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
          return Result.ok("增加成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  Result.fail("增加失败！");
    }
    /*
    * 点击修改时需要回显的数据
    * */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        System.out.println(brandService.findOne(id));
       return brandService.findOne(id);
}
    /*
    * 点击修改后并更新数据库内容，返回一个成功与否的实体类
    * */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return Result.ok("修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败！");
    }
    /*
    * 批量删除
    * */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败！");
    }

    @PostMapping("/search")
    public PageResult search(@RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "rows",defaultValue = "10")Integer rows,
                             @RequestBody TbBrand tbBrand){
       return brandService.search(page,rows,tbBrand);
    }

    /**
     * 页面加载时，查询品牌数据 并按[{id:"1",text:"华为"}]的方式返回
     * @return
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){

        return  brandService.selectOptionList();
    }

}
