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
        «var  PreStdCheckCondition preStdCheckCondition = (PreStdCheckCondition)transition.preCondition»
            «IF (preStdCheckCondition !== null && transition.preCondition.pluginName == "StdCheckPlugin")»
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
                    «IF (preStdCheckCondition.functionName !== null && preStdCheckCondition.functionName !== "NONE"))»
                        «IF (preStdCheckCondition.functionName == "isAnyChildStatus")»
                            return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                        «ELSEIF (preStdCheckCondition.functionName == "areAllChildrenStatus")»
                            return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                        «ELSEIF (preStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                            return rp->«preStdCheckCondition.functionName()»;
                        «ELSEIF (preStdCheckCondition.functionName == "amISuccessful")»
                            return rp->«preStdCheckCondition.functionName()»;
                        «ELSEIF (preStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                            return rp->«preStdCheckCondition.functionName()»;
                        «ELSEIF (preStdCheckCondition.functionName == "isStateTimedOut")»
                            return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», rp);
                        «ELSEIF (preStdCheckCondition.functionName == "isTimeOut")»
                            return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», AlicaTime::«preStdCheckCondition.parameter2», rp);
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
        «var  PreStdCheckCondition preStdCheckCondition = (PreStdCheckCondition)plan.preCondition»
        «IF (preStdCheckCondition !== null && plan.preCondition.pluginName == "StdCheckPlugin")»
            //Check of PreCondition - (Name): «plan.preCondition.name», (ConditionString): «plan.preCondition.conditionString» , (Comment) : «plan.preCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  plan.preCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PreCondition«plan.preCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (preStdCheckCondition.functionName !== null && preStdCheckCondition.functionName !== "NONE"))»
                 «IF (preStdCheckCondition.functionName == "isAnyChildStatus")»
                     return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                 «ELSEIF (preStdCheckCondition.functionName == "areAllChildrenStatus")»
                     return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                 «ELSEIF (preStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                     return rp->«preStdCheckCondition.functionName()»;
                 «ELSEIF (preStdCheckCondition.functionName == "amISuccessful")»
                     return rp->«preStdCheckCondition.functionName()»;
                 «ELSEIF (preStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                     return rp->«preStdCheckCondition.functionName()»;
                 «ELSEIF (preStdCheckCondition.functionName == "isStateTimedOut")»
                     return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», rp);
                 «ELSEIF (preStdCheckCondition.functionName == "isTimeOut")»
                     return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», AlicaTime::«preStdCheckCondition.parameter2», rp);
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
        «var  RuntimeStdCheckCondition runtimeStdCheckCondition = (RuntimeStdCheckCondition)plan.runtimeCondition»
        «IF (runtimeStdCheckCondition !== null && plan.runtimeCondition.pluginName == "StdCheckPlugin")»
            //Check of RuntimeCondition - (Name): «plan.runtimeCondition.name», (ConditionString): «plan.runtimeCondition.conditionString», (Comment) : «plan.runtimeCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables = plan.runtimeCondition.variables»«FOR variable : variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool RunTimeCondition«plan.runtimeCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp) {
               «IF (runtimeStdCheckCondition.functionName !== null && runtimeStdCheckCondition.functionName !== "NONE"))»
                    «IF (runtimeStdCheckCondition.functionName == "isAnyChildStatus")»
                        return rp->«runtimeStdCheckCondition.functionName»(PlanStatus::«runtimeStdCheckCondition.parameter1»);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "areAllChildrenStatus")»
                        return rp->«runtimeStdCheckCondition.functionName»(PlanStatus::«runtimeStdCheckCondition.parameter1»);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "amISuccessful")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isStateTimedOut")»
                        return rp->«runtimeStdCheckCondition.functionName()»(AlicaTime::«runtimeStdCheckCondition.parameter1», rp);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isTimeOut")»
                        return rp->«runtimeStdCheckCondition.functionName()»(AlicaTime::«runtimeStdCheckCondition.parameter1», AlicaTime::«runtimeStdCheckCondition.parameter2», rp);
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
        «var  PostStdCheckCondition postStdCheckCondition = (PostStdCheckCondition)terminalState.postCondition»
        «IF (postStdCheckCondition !== null && terminalState.postCondition.pluginName == "StdCheckPlugin")»
            //Check of PostCondition - (Name): «terminalState.postCondition.name», (ConditionString): «terminalState.postCondition.conditionString» , (Comment) : «terminalState.postCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  terminalState.postCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PostCondition«terminalState.postCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
                «IF (postStdCheckCondition.functionName !== null && postStdCheckCondition.functionName !== "NONE"))»
                    «IF (postStdCheckCondition.functionName == "isAnyChildStatus")»
                        return rp->«postStdCheckCondition.functionName»(PlanStatus::«postStdCheckCondition.parameter1»);
                    «ELSEIF (postStdCheckCondition.functionName == "areAllChildrenStatus")»
                        return rp->«postStdCheckCondition.functionName»(PlanStatus::«postStdCheckCondition.parameter1»);
                    «ELSEIF (postStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                        return rp->«postStdCheckCondition.functionName()»;
                    «ELSEIF (postStdCheckCondition.functionName == "amISuccessful")»
                        return rp->«postStdCheckCondition.functionName()»;
                    «ELSEIF (postStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                        return rp->«postStdCheckCondition.functionName()»;
                    «ELSEIF (postStdCheckCondition.functionName == "isStateTimedOut")»
                        return rp->«postStdCheckCondition.functionName()»(AlicaTime::«postStdCheckCondition.parameter1», rp);
                    «ELSEIF (postStdCheckCondition.functionName == "isTimeOut")»
                        return rp->«postStdCheckCondition.functionName()»(AlicaTime::«postStdCheckCondition.parameter1», AlicaTime::«postStdCheckCondition.parameter2», rp);
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
        «var RuntimeStdCheckCondition runtimeStdCheckCondition = (RuntimeStdCheckCondition)behaviour.runtimeCondition»
        «IF (runtimeStdCheckCondition !== null && behaviour.runtimeCondition.pluginName == "StdCheckPlugin")»
            //Check of RuntimeCondition - (Name): «behaviour.runtimeCondition.name», (ConditionString): «behaviour.runtimeCondition.conditionString», (Comment) : «behaviour.runtimeCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables = behaviour.runtimeCondition.variables»«FOR variable : variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool RunTimeCondition«behaviour.runtimeCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp) {
               «IF (runtimeStdCheckCondition.functionName !== null && runtimeStdCheckCondition.functionName !== "NONE"))»
                    «IF (runtimeStdCheckCondition.functionName == "isAnyChildStatus")»
                        return rp->«runtimeStdCheckCondition.functionName»(PlanStatus::«runtimeStdCheckCondition.parameter1»);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "areAllChildrenStatus")»
                        return rp->«runtimeStdCheckCondition.functionName»(PlanStatus::«runtimeStdCheckCondition.parameter1»);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "amISuccessful")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                        return rp->«runtimeStdCheckCondition.functionName()»;
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isStateTimedOut")»
                        return rp->«runtimeStdCheckCondition.functionName()»(AlicaTime::«runtimeStdCheckCondition.parameter1», rp);
                    «ELSEIF (runtimeStdCheckCondition.functionName == "isTimeOut")»
                        return rp->«runtimeStdCheckCondition.functionName()»(AlicaTime::«runtimeStdCheckCondition.parameter1», AlicaTime::«runtimeStdCheckCondition.parameter2», rp);
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
        «var PreStdCheckCondition preStdCheckCondition = (PreStdCheckCondition)behaviour.preCondition»
        «IF (preStdCheckCondition !== null && behaviour.preCondition.pluginName == "StdCheckPlugin")»
            //Check of PreCondition - (Name): «behaviour.preCondition.name», (ConditionString): «behaviour.preCondition.conditionString» , (Comment) : «behaviour.preCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  behaviour.preCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PreCondition«behaviour.preCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (preStdCheckCondition.functionName !== null && preStdCheckCondition.functionName !== "NONE"))»
                  «IF (preStdCheckCondition.functionName == "isAnyChildStatus")»
                      return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                  «ELSEIF (preStdCheckCondition.functionName == "areAllChildrenStatus")»
                      return rp->«preStdCheckCondition.functionName»(PlanStatus::«preStdCheckCondition.parameter1»);
                  «ELSEIF (preStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                      return rp->«preStdCheckCondition.functionName()»;
                  «ELSEIF (preStdCheckCondition.functionName == "amISuccessful")»
                      return rp->«preStdCheckCondition.functionName()»;
                  «ELSEIF (preStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                      return rp->«preStdCheckCondition.functionName()»;
                  «ELSEIF (preStdCheckCondition.functionName == "isStateTimedOut")»
                      return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», rp);
                  «ELSEIF (preStdCheckCondition.functionName == "isTimeOut")»
                      return rp->«preStdCheckCondition.functionName()»(AlicaTime::«preStdCheckCondition.parameter1», AlicaTime::«preStdCheckCondition.parameter2», rp);
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
        «var PostStdCheckCondition postStdCheckCondition = (PostStdCheckCondition)behaviour.postCondition»
        «IF (postStdCheckCondition !== null && behaviour.postCondition.pluginName == "StdCheckPlugin")»
            //Check of PostCondition - (Name): «behaviour.postCondition.name», (ConditionString): «behaviour.postCondition.conditionString» , (Comment) : «behaviour.postCondition.comment»

            /**
             * Available Vars:«var  List<Variable> variables =  behaviour.postCondition.variables»«FOR variable :variables»
             *	- «variable.name» («variable.id»)«ENDFOR»
             */
            bool PostCondition«behaviour.postCondition.id»::evaluate(std::shared_ptr<RunningPlan> rp)
            {
             «IF (postStdCheckCondition.functionName !== null && postStdCheckCondition.functionName !== "NONE"))»
                 «IF (postStdCheckCondition.functionName == "isAnyChildStatus")»
                     return rp->«postStdCheckCondition.functionName»(PlanStatus::«postStdCheckCondition.parameter1»);
                 «ELSEIF (postStdCheckCondition.functionName == "areAllChildrenStatus")»
                     return rp->«postStdCheckCondition.functionName»(PlanStatus::«postStdCheckCondition.parameter1»);
                 «ELSEIF (postStdCheckCondition.functionName == "isAnyChildTaskSuccessful")»
                     return rp->«postStdCheckCondition.functionName()»;
                 «ELSEIF (postStdCheckCondition.functionName == "amISuccessful")»
                     return rp->«postStdCheckCondition.functionName()»;
                 «ELSEIF (postStdCheckCondition.functionName == "amISuccessfulInAnyChild")»
                     return rp->«postStdCheckCondition.functionName()»;
                 «ELSEIF (postStdCheckCondition.functionName == "isStateTimedOut")»
                     return rp->«postStdCheckCondition.functionName()»(AlicaTime::«postStdCheckCondition.parameter1», rp);
                 «ELSEIF (postStdCheckCondition.functionName == "isTimeOut")»
                     return rp->«postStdCheckCondition.functionName()»(AlicaTime::«postStdCheckCondition.parameter1», AlicaTime::«postStdCheckCondition.parameter2», rp);
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