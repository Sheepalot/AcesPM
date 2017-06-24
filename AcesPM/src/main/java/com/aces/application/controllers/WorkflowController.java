package com.aces.application.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.aces.application.models.Node;
import com.aces.application.models.WorkflowElement;
import com.aces.application.repositories.ResponseSetRepository;
import com.aces.application.services.WorkflowService;

@Controller
@SessionAttributes("element")
public class WorkflowController {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private ResponseSetRepository responseSetRepository;
	
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
	
	@RequestMapping(value={"/addResponseModal/{elementId}"})
    public String addResponseModal(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
		m.addAttribute("allResponseSets", responseSetRepository.findAll());
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/addResponseModal";
    }

	@RequestMapping(value={"/elementDetailsModal/{elementId}"})
    public String elementDetailsModal(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/elementModal";
    }
	
	@RequestMapping(value={"/newElementModal/{parentId}"})
    public String newElementModal(Model m, @PathVariable int parentId) {	
		WorkflowElement newElement = new WorkflowElement();
		newElement.parent = workflowService.findElementById(parentId);
		m.addAttribute("element", newElement);
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/elementModal";
    }
	
	@RequestMapping(value={"/runAudit/{rootId}"})
    public String runAudit(Model m, @PathVariable int rootId) {	
		//WorkflowElement root = workflowService.findElementById(rootId);
		//m.addAttribute("element", root);
		
		
		LinkedHashMap<String, List<String>> questionMap = new LinkedHashMap<String, List<String>>();
		questionMap.put("Who is completing the audit?", new ArrayList<String>(Arrays.asList("Dietetics", "Other")));
		questionMap.put("Initial screening within 24hrs of admission to hospital?", new ArrayList<String>(Arrays.asList("Yes", "No")));
		questionMap.put("Current height present?", new ArrayList<String>(Arrays.asList("Yes", "No")));
		questionMap.put("Current height circled?", new ArrayList<String>(Arrays.asList("Actual", "Reported", "Ulna", "N/A", "Not stated")));
		
		
		m.addAttribute("questionMap", questionMap);
        return "run";
    }
	
	@RequestMapping(value={"/deleteElement/{id}"})
	@ResponseStatus(value = HttpStatus.OK)
    public void deleteElement(Model m, @PathVariable int id) {	
		WorkflowElement toDelete = workflowService.findElementById(id);
		
		//Reparent the children
		for(WorkflowElement child:toDelete.children){
			child.parent = toDelete.parent;
			workflowService.save(child);
		}
	
		workflowService.delete(toDelete);
    }
	
	@RequestMapping(value={"/editDetails"}, method=RequestMethod.POST)
    public String editDetails(@Valid @ModelAttribute("element")WorkflowElement edited, BindingResult result, ModelMap model) {
		workflowService.save(edited);
		return "redirect:/home";
    }
}
