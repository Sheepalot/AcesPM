package com.aces.application.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.aces.application.models.Memory;
import com.aces.application.models.Node;
import com.aces.application.models.ResponseSet;
import com.aces.application.models.WorkflowElement;
import com.aces.application.repositories.ResponseSetRepository;
import com.aces.application.services.MemoryService;
import com.aces.application.services.WorkflowService;
import com.aces.application.utilities.AuditManagerHolder;
import com.aces.application.utilities.Question;

@Controller
@SessionAttributes("element")
public class WorkflowController {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private ResponseSetRepository responseSetRepository;
	
	@Autowired
	private MemoryService memoryService;
	
	private String getCurrentUsername(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return ((User)auth.getPrincipal()).getUsername();
	}
	
	@RequestMapping(value={"/", "/home"})
    public String home() {
		if(!AuditManagerHolder.getManager().isRunningAudit()){
			return "home";
		}
		return "runninghome";
    }
	
	/**
	 * Grab all the top level (root) nodes
	 * @return
	 */
	@RequestMapping(value={"/roots"}, method=RequestMethod.GET)
	public @ResponseBody List<Node> roots() {
		ArrayList<WorkflowElement> elements = new ArrayList<WorkflowElement>();
		elements.addAll(workflowService.findRootWorkflowElements());
	    return elements.stream().map(elm -> elm.buildNodeView()).collect(Collectors.toList());
	}
	
	/**
	 * Load a list of all possible responses (answers)
	 * @param m
	 * @return
	 */
	@RequestMapping(value={"/responses"}, method=RequestMethod.GET)
	public String responses(Model m) {
		m.addAttribute("allResponseSets", responseSetRepository.findAll());
	    return "responses";
	}
	
	/**
	 * Load and element(question) for displaying its details
	 * @param m
	 * @param elementId
	 * @return
	 */
	@RequestMapping(value={"/elementDetails/{elementId}"})
    public String elementDetails(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
        return "fragments/elementDetails";
    }
	
	/**
	 * Provide a modal for adding a response to a question
	 * @param m
	 * @param elementId
	 * @return
	 */
	@RequestMapping(value={"/addResponseModal/{elementId}"})
    public String addResponseModal(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
		m.addAttribute("allResponseSets", responseSetRepository.findAll());
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/addResponseModal";
    }

	/**
	 * provide a modal for editing a question's details 
	 * @param m
	 * @param elementId
	 * @return
	 */
	@RequestMapping(value={"/elementDetailsModal/{elementId}"})
    public String elementDetailsModal(Model m, @PathVariable int elementId) {
		m.addAttribute("element", workflowService.findElementById(elementId));
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/elementModal";
    }
	
	/**
	 * Save edited question details
	 * @param edited
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/editDetails"}, method=RequestMethod.POST)
    public String editDetails(@Valid @ModelAttribute("element")WorkflowElement edited, BindingResult result, ModelMap model) {
		workflowService.save(edited);
		return "redirect:/home";
    }
	
	/**
	 * Provide a modal for adding a new question
	 * @param m
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value={"/newElementModal/{parentId}"})
    public String newElementModal(Model m, @PathVariable int parentId) {	
		WorkflowElement newElement = new WorkflowElement();
		newElement.parent = workflowService.findElementById(parentId);
		m.addAttribute("element", newElement);
		m.addAttribute("saveURL", "/editDetails");
        return "fragments/elementModal";
    }
	
	/**
	 * Delete a element (question)
	 * @param m
	 * @param id
	 */
	@RequestMapping(value={"/deleteElement/{id}"})
	@ResponseStatus(value = HttpStatus.OK)
    public void deleteElement(Model m, @PathVariable int id) {	
		WorkflowElement toDelete = workflowService.findElementById(id);
		
		//Can't delete roots for now
		if(toDelete.parent==null){
			return;
		}
		
		//Re-parent the children
		for(WorkflowElement child:toDelete.children){
			child.parent = toDelete.parent;
			workflowService.save(child);
		}
	
		workflowService.delete(toDelete);
    }
	
	/**
	 * Clear the currently running audit
	 * @return
	 */
	@RequestMapping(value={"/cancelAudit"})
    public String cancelAudit() {
		AuditManagerHolder.getManager().clear();
		return "redirect:/home";
    }
	
	/**
	 * Start a new audit from a given parent question
	 * @param m
	 * @param parentId
	 */
	@RequestMapping(value={"/startAudit/{parentId}"})
	@ResponseStatus(value = HttpStatus.OK)
	public void startAudit(Model m, @PathVariable int parentId) {	
		WorkflowElement auditRoot  = workflowService.findElementById(parentId);
		AuditManagerHolder.getManager().populate(auditRoot);
    }
	
	/**
	 * Display a modal for adding a new response (answer)
	 * @param m
	 * @return
	 */
	@RequestMapping(value={"/newResponseModal"})
    public String newResponseModal(Model m) {	
		ResponseSet newResponseSet = new ResponseSet();
		m.addAttribute("element", newResponseSet);
		m.addAttribute("saveURL", "/saveNewResponseSet");
        return "fragments/elementModal";
    }
	
	/**
	 * Save a new possible answer to the database
	 * @param edited
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/saveNewResponseSet"}, method=RequestMethod.POST)
    public String saveNewResponseSet(@Valid @ModelAttribute("element")ResponseSet edited, BindingResult result, ModelMap model) {
		edited.setType("TEXT");
		responseSetRepository.save(edited);
		return "redirect:/responses";
    }
	
	/**
	 * Delete a possible answer
	 * @param responseId
	 * @return
	 */
	@RequestMapping(value={"/deleteResponse"}, method=RequestMethod.POST)
    public String deleteResponse(@RequestParam(value="responseId") int responseId) {
		responseSetRepository.delete(responseId);
		return "redirect:/responses";
    }
	
	/**
	 * Start providing data for an audit
	 * @param m
	 * @return
	 */
	@RequestMapping(value={"/runAudit"})
    public String runAudit(Model m) {
		LinkedHashMap<String, List<String>> questionMap = new LinkedHashMap<String, List<String>>();
		AuditManagerHolder.getManager().auditing.get(getCurrentUsername()).forEach(e -> {
			questionMap.put(e.title, e.anwsers);
		});
		m.addAttribute("questionMap", questionMap);
        return "run";
    }
	
	
	/**
	 * Submit a single entry for an audit
	 * @param answers
	 */
	@RequestMapping(value={"/submitAudit"}, method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
    public void submitAudit(@RequestParam(value="answers[]") String[] answers) {
		AuditManagerHolder.getManager().submitAnswers(answers);
		
		Memory m = memoryService.get();
		m.setManager(AuditManagerHolder.getManager());
		memoryService.save(m);
    }
	
	/**
	 * Display the results for the audit currently in progress
	 * @param m
	 * @return
	 */
	@RequestMapping(value={"/results"})
    public String results(Model m) {
		LinkedHashMap<String, List<Question>> results = new LinkedHashMap<String, List<Question>>();
		AuditManagerHolder.getManager().auditing.entrySet().forEach(e -> {
			if(e.getKey().startsWith(getCurrentUsername()) && e.getKey().length() > getCurrentUsername().length()){
				results.put(e.getKey().split(" ")[1], e.getValue().subList(1, e.getValue().size()));
			}
		});
		m.addAttribute("results",results);
		return "results";
    }
}
