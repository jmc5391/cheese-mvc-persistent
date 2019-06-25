package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "All Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {

        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String process(@ModelAttribute @Valid Menu newMenu, Errors errors, Model model) {

        if(errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model) {

        model.addAttribute("menu", menuDao.findOne(menuId));
        return "menu/view";

    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuId, Model model) {

        model.addAttribute("menu", menuDao.findOne(menuId));
        AddMenuItemForm form = new AddMenuItemForm(menuDao.findOne(menuId), cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add Item to Menu: " + menuDao.findOne(menuId).getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm form, int menuId, Errors errors, Model model) {

        if(errors.hasErrors()) {
            model.addAttribute("menu", menuDao.findOne(menuId));
            AddMenuItemForm oldForm = new AddMenuItemForm(menuDao.findOne(menuId), cheeseDao.findAll());
            model.addAttribute("form", oldForm);
            model.addAttribute("title", "Add Item to Menu: " + menuDao.findOne(menuId).getName());

            return "menu/add-item";
        }

        Menu requestedMenu = menuDao.findOne(menuId);
        Cheese requestedCheese = cheeseDao.findOne(form.getCheeseId());
        requestedMenu.addItem(requestedCheese);
        menuDao.save(requestedMenu);

        return "redirect:/menu/view/" + menuId;
    }
}
