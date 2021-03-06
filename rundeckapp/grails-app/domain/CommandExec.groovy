
/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.springframework.validation.Errors
import com.dtolabs.rundeck.execution.IWorkflowCmdItem

/*
* CommandExec.java
*
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Feb 25, 2010 3:02:01 PM
* $Id$
*/

public class CommandExec extends ExecutionContext implements IWorkflowCmdItem {
    String returnProperty
    String ifString
    String unlessString
    String equalsString
    static belongsTo = [workflow: Workflow]

    public String toString() {
        StringBuffer sb = new StringBuffer()
        sb << "command( "
        sb << (returnProperty ? "return=\"${returnProperty}\"" : '')
        sb << (ifString ? " if=\"${ifString}\"" : '')
        sb << (unlessString ? "unless=\"${unlessString}\"" : '')
        sb << (equalsString ? "equals=\"${equalsString}\"" : '')
        sb << (adhocRemoteString ? "exec: ${adhocRemoteString}" : '')
        sb << (adhocLocalString ? "script: ${adhocLocalString}" : '')
        sb << (adhocFilepath ? "scriptfile: ${adhocFilepath}" : '')
        sb << (argString ? "scriptargs: ${argString}" : '')
        sb<<")"

        return sb.toString()
    }

    static constraints = {
        returnProperty(nullable: true)
        ifString(nullable: true)
        unlessString(nullable: true)
        equalsString(nullable: true)
        project(nullable: true)
        argString(nullable: true)
        user(nullable: true)
        adhocRemoteString(nullable:true)
        adhocLocalString(nullable:true)
        adhocFilepath(nullable:true)

    }

    public CommandExec createClone(){
        CommandExec ce = new CommandExec(this.properties)
        return ce
    }
}