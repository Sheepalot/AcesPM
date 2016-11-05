package com.aces.application.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.aces.application.models.Node;
import com.aces.application.models.WorkflowElement;
import com.aces.application.services.WorkflowService;

@Controller
@SessionAttributes("element")
public class WorkflowController {
	
	@Autowired
	private WorkflowService workflowService;
	
	@RequestMapping(value={"/", "/home"})
    public String home() {
        return "home";
    }
	
	@RequestMapping(value={"/roots"}, method=RequestMethod.GET)
	public @ResponseBody List<Node> roots() {
		ArrayList<WorkflowElement> elements = new ArrayList<WorkflowElement>();
		elements.addAll(workflowService.findRootWorkflowElements());
	    return elements.stream().map(elm -> elm.buildNodeView()).collect(Collectors.toList());
	}
	
	@RequestMapping(value={"/elementDetails/{elementId}"})
    public String elementDetails(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
        return "fragments/elementDetails";
    }

	@RequestMapping(value={"/elementDetailsModal/{elementId}"})
    public String elementDetailsModal(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
        return "fragments/elementModal";
    }
	
	@RequestMapping(value={"/editDetails"}, method=RequestMethod.POST)
    public String editDetails(@Valid @ModelAttribute("element")WorkflowElement edited, BindingResult result, ModelMap model) {
		workflowService.save(edited);
		return "redirect:/home";
    }
}
