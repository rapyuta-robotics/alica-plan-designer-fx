package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.planDesigner.alicamodel.Plan
import de.unikassel.vs.alica.planDesigner.alicamodel.Behaviour
import de.unikassel.vs.alica.planDesigner.alicamodel.State
import de.unikassel.vs.alica.planDesigner.alicamodel.ConfAbstractPlanWrapper
import de.unikassel.vs.alica.planDesigner.alicamodel.TerminalState
import de.unikassel.vs.alica.planDesigner.alicamodel.Transition
import de.unikassel.vs.alica.planDesigner.alicamodel.Variable
import de.unikassel.vs.alica.planDesigner.alicamodel.Quantifier
import java.util.Map
import java.util.List
import de.unikassel.vs.alica.planDesigner.alicamodel.EntryPoint
import de.unikassel.vs.alica.planDesigner.alicamodel.AbstractPlan

class StdCheckTemplate {
    private Map<String, String> protectedRegions;

    public def void setProtectedRegions (Map<String, String> regions) {
        protectedRegions = regions;
    }

    def String expressionsStateCheckingMethods(State state) '''
        «var  List<Transition> outTransitions = state.outTransitions»
        «FOR transition : outTransitions»
            «IF (transition.preCondition !== null && transition.preCondition.pluginName == "StdCheckPlugin")»
                /**
                * Outgoing transition:
                *   - Name: «transition.preCondition.name», ConditionString: «transition.preCondition.conditionString», Comment: «transition.comment»
                *
                * Abstract plans in current state: «var  List<ConfAbstractPlanWrapper> wrappers = state.confAbstractPlanWrappers»
                «FOR wrapper : wrappers»
                *   - «wrapper.abstractPlan.name» («wrapper.abstractPlan.id»)
                «ENDFOR»
                *
                * Tasks in plan: «var  List<EntryPoint> entryPoints = state.parentPlan.entryPoints»
                «FOR planEntryPoint : entryPoints»
                *   - «planEntryPoint.task.name» («planEntryPoint.task.id») (Entrypoint: «planEntryPoint.id»)«ENDFOR»
                *
                * States in plan: «var  List<State> states = state.parentPlan.states»
                «FOR stateOfInPlan : states»
                *   - «stateOfInPlan.name» («stateOfInPlan.id»)
                «ENDFOR»
                *
                * Variables of precondition:«var  List<Variable> variables =    transition.preCondition.variables»
                «FOR variable : variables»
                *	- «variable.name» («variable.id»)
                «ENDFOR»
                */
                bool PreCondition«transition.preCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
                 {
                    «IF (transition.preCondition.functionName !== null && transition.preCondition.functionName !== "NONE"))»
                        «IF (transition.preCondition.functionName == "isAnyChildStatus")»
                            return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                        «ELSEIF (transition.preCondition.functionName == "areAllChildrenStatus")»
                            return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                        «ELSEIF (transition.preCondition.functionName == "isAnyChildTaskSuccessful")»
                            return rp->«transition.preCondition.functionName()»;
                        «ELSEIF (transition.preCondition.functionName == "amISuccessful")»
                            return rp->«transition.preCondition.functionName()»;
                        «ELSEIF (transition.preCondition.functionName == "amISuccessfulInAnyChild")»
                            return rp->«transition.preCondition.functionName()»;
                        «ELSEIF (transition.preCondition.functionName == "isStateTimedOut")»
                            return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», rp);
                        «ELSEIF (transition.preCondition.functionName == "isTimeOut")»
                            return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», AlicaTime::«transition.preCondition.parameter2», rp);
                        «ELSE»
                            std::cout << "No Function  is being selected for PreCondition «transition.preCondition.id» in Transition '«transition.getName»' in the UI" << std::endl;
                        «ENDIF»
                    «ELSE»
                        /*PROTECTED REGION ID(«transition.id») ENABLED START*/
                        «IF (protectedRegions.containsKey(transition.id + ""))»
                            «protectedRegions.get(transition.id + "")»
                        «ELSE»
                        std::cout << "The PreCondition «transition.preCondition.id» in Transition '«transition.getName»' is not implement yet!" << std::endl;
                        return false;
                        «ENDIF»
                        /*PROTECTED REGION END*/
                    «ENDIF»
                }
            «ENDIF»
        «ENDFOR»
    '''

    def String expressionsPlanCheckingMethods(Plan plan) '''
        «IF (plan.preCondition !== null && plan.preCondition.pluginName == "StdCheckPlugin")»
            //Check of PreCondition - (Name): «plan.preCondition.name», (ConditionString): «plan.preCondition.conditionString» , (Comment) : «plan.preCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  plan.preCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PreCondition«plan.preCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (transition.preCondition.functionName !== null && transition.preCondition.functionName !== "NONE"))»
                 «IF (transition.preCondition.functionName == "isAnyChildStatus")»
                     return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                 «ELSEIF (transition.preCondition.functionName == "areAllChildrenStatus")»
                     return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                 «ELSEIF (transition.preCondition.functionName == "isAnyChildTaskSuccessful")»
                     return rp->«transition.preCondition.functionName()»;
                 «ELSEIF (transition.preCondition.functionName == "amISuccessful")»
                     return rp->«transition.preCondition.functionName()»;
                 «ELSEIF (transition.preCondition.functionName == "amISuccessfulInAnyChild")»
                     return rp->«transition.preCondition.functionName()»;
                 «ELSEIF (transition.preCondition.functionName == "isStateTimedOut")»
                     return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», rp);
                 «ELSEIF (transition.preCondition.functionName == "isTimeOut")»
                     return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», AlicaTime::«transition.preCondition.parameter2», rp);
                 «ELSE»
                     std::cout << "No Function is being selected for PreCondition «plan.preCondition.id» in Plan '«plan.getName»' in the UI" << std::endl;
                 «ENDIF»
             «ELSE»
                /*PROTECTED REGION ID(«plan.preCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey(plan.preCondition.id + ""))»
                    «protectedRegions.get(plan.preCondition.id + "")»
                «ELSE»
                    std::cout << "The PreCondition «plan.preCondition.id» in Plan '«plan.getName»' is not implement yet!" << std::endl;
                    return false;
                «ENDIF»
                /*PROTECTED REGION END*/
             «ENDIF»
            }
        «ENDIF»
        «IF (plan.runtimeCondition !== null && plan.runtimeCondition.pluginName == "StdCheckPlugin")»
            //Check of RuntimeCondition - (Name): «plan.runtimeCondition.name», (ConditionString): «plan.runtimeCondition.conditionString», (Comment) : «plan.runtimeCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables = plan.runtimeCondition.variables»«FOR variable : variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool RunTimeCondition«plan.runtimeCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp) {
               «IF (transition.runtimeCondition.functionName !== null && transition.runtimeCondition.functionName !== "NONE"))»
                    «IF (transition.runtimeCondition.functionName == "isAnyChildStatus")»
                        return rp->«transition.runtimeCondition.functionName»(PlanStatus::«transition.runtimeCondition.parameter1»);
                    «ELSEIF (transition.runtimeCondition.functionName == "areAllChildrenStatus")»
                        return rp->«transition.runtimeCondition.functionName»(PlanStatus::«transition.runtimeCondition.parameter1»);
                    «ELSEIF (transition.runtimeCondition.functionName == "isAnyChildTaskSuccessful")»
                        return rp->«transition.runtimeCondition.functionName()»;
                    «ELSEIF (transition.runtimeCondition.functionName == "amISuccessful")»
                        return rp->«transition.runtimeCondition.functionName()»;
                    «ELSEIF (transition.runtimeCondition.functionName == "amISuccessfulInAnyChild")»
                        return rp->«transition.runtimeCondition.functionName()»;
                    «ELSEIF (transition.runtimeCondition.functionName == "isStateTimedOut")»
                        return rp->«transition.runtimeCondition.functionName()»(AlicaTime::«transition.runtimeCondition.parameter1», rp);
                    «ELSEIF (transition.runtimeCondition.functionName == "isTimeOut")»
                        return rp->«transition.runtimeCondition.functionName()»(AlicaTime::«transition.runtimeCondition.parameter1», AlicaTime::«transition.runtimeCondition.parameter2», rp);
                    «ELSE»
                        std::cout << "No Function is being selected for RuntimeCondition «plan.runtimeCondition.id» in Plan '«plan.getName»' in the UI" << std::endl;
                    «ENDIF»
               «ELSE»
                    /*PROTECTED REGION ID(«plan.runtimeCondition.id») ENABLED START*/
                    «IF (protectedRegions.containsKey(plan.runtimeCondition.id + ""))»
                        «protectedRegions.get(plan.runtimeCondition.id + "")»
                    «ELSE»
                        std::cout << "The RunTimeCondition «plan.runtimeCondition.id» in Plan '«plan.getName»' is not implement yet!" << std::endl;
                        return false;
                    «ENDIF»
                    /*PROTECTED REGION END*/
               «ENDIF»
            }
        «ENDIF»
        «var  List<State> states =  plan.states»
        «FOR state : states»
        «IF (state instanceof TerminalState)»
        «var TerminalState terminalState = state as TerminalState»
        «IF (terminalState.postCondition !== null && terminalState.postCondition.pluginName == "StdCheckPlugin")»
            //Check of PostCondition - (Name): «terminalState.postCondition.name», (ConditionString): «terminalState.postCondition.conditionString» , (Comment) : «terminalState.postCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  terminalState.postCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PostCondition«terminalState.postCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
                «IF (transition.postCondition.functionName !== null && transition.postCondition.functionName !== "NONE"))»
                    «IF (transition.postCondition.functionName == "isAnyChildStatus")»
                        return rp->«transition.postCondition.functionName»(PlanStatus::«transition.postCondition.parameter1»);
                    «ELSEIF (transition.postCondition.functionName == "areAllChildrenStatus")»
                        return rp->«transition.postCondition.functionName»(PlanStatus::«transition.postCondition.parameter1»);
                    «ELSEIF (transition.postCondition.functionName == "isAnyChildTaskSuccessful")»
                        return rp->«transition.postCondition.functionName()»;
                    «ELSEIF (transition.postCondition.functionName == "amISuccessful")»
                        return rp->«transition.postCondition.functionName()»;
                    «ELSEIF (transition.postCondition.functionName == "amISuccessfulInAnyChild")»
                        return rp->«transition.postCondition.functionName()»;
                    «ELSEIF (transition.postCondition.functionName == "isStateTimedOut")»
                        return rp->«transition.postCondition.functionName()»(AlicaTime::«transition.postCondition.parameter1», rp);
                    «ELSEIF (transition.postCondition.functionName == "isTimeOut")»
                        return rp->«transition.postCondition.functionName()»(AlicaTime::«transition.postCondition.parameter1», AlicaTime::«transition.postCondition.parameter2», rp);
                    «ELSE»
                        std::cout << "No Function is being selected for PostCondition «terminalState.postCondition.id» in TerminalState '«terminalState.getName»' in the UI" << std::endl;
                    «ENDIF»
                «ELSE»
                    /*PROTECTED REGION ID(«terminalState.postCondition.id») ENABLED START*/
                    «IF (protectedRegions.containsKey(terminalState.postCondition.id + ""))»
                        «protectedRegions.get(terminalState.postCondition.id + "")»
                    «ELSE»
                        std::cout << "The PostCondition «terminalState.postCondition.id» in TerminalState '«terminalState.getName»' is not implement yet!" << std::endl;
                        std::cout << "However, PostConditions are a feature that makes sense in the context of planning, which is not supported by ALICA, yet! So don't worry." << std::endl;
                        return false;
                    «ENDIF»
                    /*PROTECTED REGION END*/
                «ENDIF»
            }
        «ENDIF»
        «ENDIF»
        «ENDFOR»
    '''

    def String expressionsBehaviourCheckingMethods(Behaviour behaviour) '''
        «IF (behaviour.runtimeCondition !== null && behaviour.runtimeCondition.pluginName == "StdCheckPlugin")»
            //Check of RuntimeCondition - (Name): «behaviour.runtimeCondition.name», (ConditionString): «behaviour.runtimeCondition.conditionString», (Comment) : «behaviour.runtimeCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables = behaviour.runtimeCondition.variables»«FOR variable : variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool RunTimeCondition«behaviour.runtimeCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp) {
                bool RunTimeCondition«plan.runtimeCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp) {
                   «IF (transition.runtimeCondition.functionName !== null && transition.runtimeCondition.functionName !== "NONE"))»
                        «IF (transition.runtimeCondition.functionName == "isAnyChildStatus")»
                            return rp->«transition.runtimeCondition.functionName»(PlanStatus::«transition.runtimeCondition.parameter1»);
                        «ELSEIF (transition.runtimeCondition.functionName == "areAllChildrenStatus")»
                            return rp->«transition.runtimeCondition.functionName»(PlanStatus::«transition.runtimeCondition.parameter1»);
                        «ELSEIF (transition.runtimeCondition.functionName == "isAnyChildTaskSuccessful")»
                            return rp->«transition.runtimeCondition.functionName()»;
                        «ELSEIF (transition.runtimeCondition.functionName == "amISuccessful")»
                            return rp->«transition.runtimeCondition.functionName()»;
                        «ELSEIF (transition.runtimeCondition.functionName == "amISuccessfulInAnyChild")»
                            return rp->«transition.runtimeCondition.functionName()»;
                        «ELSEIF (transition.runtimeCondition.functionName == "isStateTimedOut")»
                            return rp->«transition.runtimeCondition.functionName()»(AlicaTime::«transition.runtimeCondition.parameter1», rp);
                        «ELSEIF (transition.runtimeCondition.functionName == "isTimeOut")»
                            return rp->«transition.runtimeCondition.functionName()»(AlicaTime::«transition.runtimeCondition.parameter1», AlicaTime::«transition.runtimeCondition.parameter2», rp);
                        «ELSE»
                            std::cout << "No Function is being selected for RuntimeCondition «behaviour.runtimeCondition.id» in Behaviour «behaviour.getName» in the UI" << std::endl;
                        «ENDIF»
                   «ELSE»
                        /*PROTECTED REGION ID(«behaviour.runtimeCondition.id») ENABLED START*/
                        «IF (protectedRegions.containsKey(behaviour.runtimeCondition.id + ""))»
                            «protectedRegions.get(behaviour.runtimeCondition.id + "")»
                        «ELSE»
                            std::cout << "The RuntimeCondition «behaviour.runtimeCondition.id» in Behaviour «behaviour.getName» is not implement yet!" << std::endl;
                            return false;
                        «ENDIF»
                        /*PROTECTED REGION END*/
                «ENDIF»
            }
        «ENDIF»
        «IF (behaviour.preCondition !== null && behaviour.preCondition.pluginName == "StdCheckPlugin")»
            //Check of PreCondition - (Name): «behaviour.preCondition.name», (ConditionString): «behaviour.preCondition.conditionString» , (Comment) : «behaviour.preCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  behaviour.preCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PreCondition«behaviour.preCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (transition.preCondition.functionName !== null && transition.preCondition.functionName !== "NONE"))»
                  «IF (transition.preCondition.functionName == "isAnyChildStatus")»
                      return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                  «ELSEIF (transition.preCondition.functionName == "areAllChildrenStatus")»
                      return rp->«transition.preCondition.functionName»(PlanStatus::«transition.preCondition.parameter1»);
                  «ELSEIF (transition.preCondition.functionName == "isAnyChildTaskSuccessful")»
                      return rp->«transition.preCondition.functionName()»;
                  «ELSEIF (transition.preCondition.functionName == "amISuccessful")»
                      return rp->«transition.preCondition.functionName()»;
                  «ELSEIF (transition.preCondition.functionName == "amISuccessfulInAnyChild")»
                      return rp->«transition.preCondition.functionName()»;
                  «ELSEIF (transition.preCondition.functionName == "isStateTimedOut")»
                      return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», rp);
                  «ELSEIF (transition.preCondition.functionName == "isTimeOut")»
                      return rp->«transition.preCondition.functionName()»(AlicaTime::«transition.preCondition.parameter1», AlicaTime::«transition.preCondition.parameter2», rp);
                  «ELSE»
                      std::cout << "No Function is being selected for PreCondition «behaviour.preCondition.id» in Behaviour «behaviour.getName» in the UI" << std::endl;
                  «ENDIF»
             «ELSE»
                /*PROTECTED REGION ID(«behaviour.preCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey(behaviour.preCondition.id + ""))»
                    «protectedRegions.get(behaviour.preCondition.id + "")»
                «ELSE»
                    std::cout << "The PreCondition «behaviour.preCondition.id» in Behaviour «behaviour.getName» is not implement yet!" << std::endl;
                    return false;
                «ENDIF»
                /*PROTECTED REGION END*/
             «ENDIF»
            }
        «ENDIF»
        «IF (behaviour.postCondition !== null && behaviour.postCondition.pluginName == "StdCheckPlugin")»
            //Check of PostCondition - (Name): «behaviour.postCondition.name», (ConditionString): «behaviour.postCondition.conditionString» , (Comment) : «behaviour.postCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  behaviour.postCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PostCondition«behaviour.postCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (transition.postCondition.functionName !== null && transition.postCondition.functionName !== "NONE"))»
                 «IF (transition.postCondition.functionName == "isAnyChildStatus")»
                     return rp->«transition.postCondition.functionName»(PlanStatus::«transition.postCondition.parameter1»);
                 «ELSEIF (transition.postCondition.functionName == "areAllChildrenStatus")»
                     return rp->«transition.postCondition.functionName»(PlanStatus::«transition.postCondition.parameter1»);
                 «ELSEIF (transition.postCondition.functionName == "isAnyChildTaskSuccessful")»
                     return rp->«transition.postCondition.functionName()»;
                 «ELSEIF (transition.postCondition.functionName == "amISuccessful")»
                     return rp->«transition.postCondition.functionName()»;
                 «ELSEIF (transition.postCondition.functionName == "amISuccessfulInAnyChild")»
                     return rp->«transition.postCondition.functionName()»;
                 «ELSEIF (transition.postCondition.functionName == "isStateTimedOut")»
                     return rp->«transition.postCondition.functionName()»(AlicaTime::«transition.postCondition.parameter1», rp);
                 «ELSEIF (transition.postCondition.functionName == "isTimeOut")»
                     return rp->«transition.postCondition.functionName()»(AlicaTime::«transition.postCondition.parameter1», AlicaTime::«transition.postCondition.parameter2», rp);
                 «ELSE»
                     std::cout << "No Function is being selected for PostCondition «behaviour.postCondition.id» in Behaviour '«behaviour.getName»' in the UI" << std::endl;
                 «ENDIF»
             «ELSE»
                /*PROTECTED REGION ID(«behaviour.postCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey(behaviour.postCondition.id + ""))»
                    «protectedRegions.get(behaviour.postCondition.id + "")»
                «ELSE»
                    std::cout << "The PostCondition «behaviour.postCondition.id» in Behaviour '«behaviour.getName»' is not implement, yet!" << std::endl;
                    return false;
                «ENDIF»
                /*PROTECTED REGION END*/
             «ENDIF»
            }
        «ENDIF»
    '''

    def String constraintPlanCheckingMethods(Plan plan) '''
        «IF (plan.runtimeCondition !== null && plan.runtimeCondition.pluginName == "StdCheckPlugin")»
        «IF (plan.runtimeCondition.variables.size > 0) || (plan.runtimeCondition.quantifiers.size > 0)»
            /**
             * RuntimeCondition - (Name): «plan.runtimeCondition.name»
             * (ConditionString): «plan.runtimeCondition.conditionString»
            «var  List<Variable> variables =  plan.runtimeCondition.variables»
            «IF (variables !== null)»
                 * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
            «ENDIF»
             * Domain Variables:
            «var  List<Quantifier> quantifiers =  plan.runtimeCondition.quantifiers»
            «IF (quantifiers !== null)»
                «FOR q : quantifiers»
                    «var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                «ENDFOR»
            «ENDIF»
            *
            */
            void Constraint«plan.runtimeCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                /*PROTECTED REGION ID(cc«plan.runtimeCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey("cc" + plan.runtimeCondition.id))»
                    «protectedRegions.get("cc" + plan.runtimeCondition.id)»
                «ELSE»
                    //Please describe your runtime constraint here
                «ENDIF»
                /*PROTECTED REGION END*/
            }
        «ENDIF»

        «ENDIF»
        «IF (plan.preCondition !== null && plan.preCondition.pluginName == "StdCheckPlugin")»
        «IF (plan.preCondition.variables.size > 0) || (plan.preCondition.quantifiers.size > 0)»
            /**
             * PreCondition - (Name): «plan.preCondition.name»
             * (ConditionString): «plan.preCondition.conditionString»
            «var  List<Variable> variables =  plan.preCondition.variables»
             * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
             * Domain Variables:
            «var  List<Quantifier> quantifiers =  plan.preCondition.quantifiers»
            «IF (quantifiers !== null)»
                «FOR q : quantifiers»
                    «var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                «ENDFOR»
            «ENDIF»
             *
             */
            void Constraint«plan.preCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                /*PROTECTED REGION ID(cc«plan.preCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey("cc" + plan.preCondition.id))»
                    «protectedRegions.get("cc" + plan.preCondition.id)»
                «ELSE»
                    //Please describe your precondition constraint here
                «ENDIF»
                /*PROTECTED REGION END*/
            }
        «ENDIF»
        «ENDIF»
    '''

    def String constraintBehaviourCheckingMethods(Behaviour behaviour) '''
        «IF (behaviour.runtimeCondition !== null && behaviour.runtimeCondition.pluginName == "StdCheckPlugin")»
        «IF (behaviour.runtimeCondition.variables.size > 0) || (behaviour.runtimeCondition.quantifiers.size > 0)»
            /**
             * RuntimeCondition - (Name): «behaviour.runtimeCondition.name»
             * (ConditionString): «behaviour.runtimeCondition.conditionString»
            «var  List<Variable> variables =  behaviour.runtimeCondition.variables»
            «IF (variables !== null)»
                 * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
            «ENDIF»
             * Domain Variables:
            «var  List<Quantifier> quantifiers =  behaviour.runtimeCondition.quantifiers»
            «IF (quantifiers !== null)»
                «FOR q : quantifiers»
                    «var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                «ENDFOR»
            «ENDIF»
            *
            */
            void Constraint«behaviour.runtimeCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                /*PROTECTED REGION ID(cc«behaviour.runtimeCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey("cc" + behaviour.runtimeCondition.id))»
                    «protectedRegions.get("cc" + behaviour.runtimeCondition.id)»
                «ELSE»
                    //Please describe your runtime constraint here
                «ENDIF»
                /*PROTECTED REGION END*/
            }
        «ENDIF»
        «ENDIF»
        «IF (behaviour.preCondition !== null && behaviour.preCondition.pluginName == "StdCheckPlugin")»
        «IF (behaviour.preCondition.variables.size > 0) || (behaviour.preCondition.quantifiers.size > 0)»
            /**
             * PreCondition - (Name): «behaviour.preCondition.name»
             * (ConditionString): «behaviour.preCondition.conditionString»
            «var  List<Variable> variables =  behaviour.preCondition.variables»
             * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
             * Domain Variables:
            «var  List<Quantifier> quantifiers =  behaviour.preCondition.quantifiers»
            «IF (quantifiers !== null)»
                «FOR q : quantifiers»
                    «var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                «ENDFOR»
            «ENDIF»
             *
             */
            void Constraint«behaviour.preCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                /*PROTECTED REGION ID(cc«behaviour.preCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey("cc" + behaviour.preCondition.id))»
                    «protectedRegions.get("cc" + behaviour.preCondition.id)»
                «ELSE»
                    //Please describe your precondition constraint here
                «ENDIF»
                /*PROTECTED REGION END*/
            }
        «ENDIF»
        «ENDIF»
        «IF (behaviour.postCondition !== null && behaviour.postCondition.pluginName == "StdCheckPlugin")»
         «IF (behaviour.postCondition.variables.size > 0) || (behaviour.postCondition.quantifiers.size > 0)»
            /**
             * PostCondition - (Name): «behaviour.postCondition.name»
             * (ConditionString): «behaviour.postCondition.conditionString»
            «var  List<Variable> variables =  behaviour.postCondition.variables»
            «IF (variables !== null)»
                 * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
            «ENDIF»
             * Domain Variables:
            «var  List<Quantifier> quantifiers =  behaviour.postCondition.quantifiers»
            «IF (quantifiers !== null)»
                «FOR q : quantifiers»
                    «var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                «ENDFOR»
            «ENDIF»
            *
            */
            void Constraint«behaviour.postCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                /*PROTECTED REGION ID(cc«behaviour.postCondition.id») ENABLED START*/
                «IF (protectedRegions.containsKey("cc" + behaviour.postCondition.id))»
                    «protectedRegions.get("cc" + behaviour.postCondition.id)»
                «ELSE»
                    //Please describe your runtime constraint here
                «ENDIF»
                /*PROTECTED REGION END*/
            }
        «ENDIF»
        «ENDIF»
    '''

    def String constraintStateCheckingMethods(State state) '''
        // State: «state.name»
        «var  List<Transition> outTransitions = state.outTransitions»
        «FOR transition : outTransitions»
            «IF transition.preCondition !== null && transition.preCondition.pluginName == "StdCheckPlugin"»
                «IF (transition.preCondition.variables.size > 0) || (transition.preCondition.quantifiers.size > 0)»
                    /**
                    * Transition:
                    * - Name: «transition.preCondition.name»
                    * - Comment: «transition.preCondition.comment»
                    * - ConditionString: «transition.preCondition.conditionString»
                    *
                    * «var  List<ConfAbstractPlanWrapper> wrappers = state.confAbstractPlanWrappers»
                    * AbstractPlans in State: «FOR wrapper : wrappers»
                    * - AbstractPlan Name: «wrapper.abstractPlan.name», PlanID: «wrapper.abstractPlan.id» «ENDFOR»
                    «var  List<Variable> variables =  transition.preCondition.variables»
                    «IF (variables !== null)»
                         * Static Variables: «FOR variable : variables»«variable.name» «ENDFOR»
                    «ENDIF»
                    * Domain Variables:
                    «IF transition.preCondition.quantifiers !== null && transition.preCondition.quantifiers.size > 0»
                        «var  List<Quantifier> quantifiers = transition.preCondition.quantifiers»
                        «FOR q : quantifiers»«var  List<String> sorts=  q.sorts»
                    * forall agents in «q.scope.name» let v = «sorts»
                        «ENDFOR»
                    «ENDIF»
                     */
                    void Constraint«transition.preCondition.id»::getConstraint(std::shared_ptr<ProblemDescriptor> c, std::shared_ptr<RunningPlan> rp) {
                        /*PROTECTED REGION ID(cc«transition.preCondition.id») ENABLED START*/
                        «IF (protectedRegions.containsKey("cc" + transition.preCondition.id))»
                            «protectedRegions.get("cc" + transition.preCondition.id)»
                        «ELSE»
                            //Please describe your precondition constraint here
                        «ENDIF»
                        /*PROTECTED REGION END*/
                    }
                «ENDIF»
            «ENDIF»
        «ENDFOR»
    '''
}