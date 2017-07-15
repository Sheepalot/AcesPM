package com.aces.application.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.aces.application.models.Node;
import com.aces.application.models.ResponseSet;
import com.aces.application.models.WorkflowElement;
import com.aces.application.repositories.ResponseSetRepository;
import com.aces.application.services.WorkflowService;
import com.aces.application.utilities.RunningAuditManager;

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
	
	@RequestMapping(value={"/responses"}, method=RequestMethod.GET)
	public String responses(Model m) {
		m.addAttribute("allResponseSets", responseSetRepository.findAll());
	    return "responses";
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
	
	@RequestMapping(value={"/startAudit/{parentId}"})
	@ResponseStatus(value = HttpStatus.OK)
    public void startAudit(Model m, @PathVariable int parentId) {	
		WorkflowElement auditRoot  = workflowService.findElementById(parentId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		RunningAuditManager.populate(((User)auth.getPrincipal()).getUsername(), auditRoot);
		RunningAuditManager.report();
    }
	
	@RequestMapping(value={"/newResponseModal"})
    public String newResponseModal(Model m) {	
		ResponseSet newResponseSet = new ResponseSet();
		m.addAttribute("element", newResponseSet);
		m.addAttribute("saveURL", "/saveNewResponseSet");
        return "fragments/elementModal";
    }
	
	@RequestMapping(value={"/saveNewResponseSet"}, method=RequestMethod.POST)
    public String saveNewResponseSet(@Valid @ModelAttribute("element")ResponseSet edited, BindingResult result, ModelMap model) {
		edited.setType("TEXT");
		responseSetRepository.save(edited);
		return "redirect:/responses";
    }
	
	@RequestMapping(value={"/deleteResponse"}, method=RequestMethod.POST)
    public String deleteResponse(@RequestParam(value="responseId") int responseId) {
		responseSetRepository.delete(responseId);
		return "redirect:/responses";
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
	
	@RequestMapping(value={"/submitAudit"}, method=RequestMethod.POST)
    public void submitAudit(@RequestParam(value="answers[]") String[] answers) {
		System.out.println("hello there");
    }
}
