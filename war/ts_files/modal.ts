import {Dependency, DependencyType, loadDependencies} from "./dependencyloader.js";

let dependencies: Dependency[] =
        [
            {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha/css/bootstrap.min.css", type: DependencyType.STYLESHEET},
            {uri: "https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js", type: DependencyType.SCRIPT, async: false},
            {uri: "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js", type: DependencyType.SCRIPT, async: false},
            {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha/js/bootstrap.min.js", type: DependencyType.SCRIPT, async: false}
        ]
let modalContainerId = "dynamic-modal-container";
let modalId = "dynamic-modal";
let modalCloseButtonId = "dynamic-modal-button"
export function displayModal(title: string, body: string, footer: string = "")
{
    if(!document.getElementById(modalContainerId))
    {
        loadDependencies(dependencies);
        let modalHtml =
                `
<link rel="stylesheet" href="/cssfiles/modalStyle.css">
<div class="modal fade" id="${modalId}">
    <div class="modal-dialog">
        <div class="modal-content">
        
            <div class= "modal-header">
                <h4 class="modal-title">${title}</h4>
                <button id="${modalCloseButtonId}" type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            
            
            <div class="modal-body">
                ${body}
            </div>
            
            <div class="modal-footer dynamic-modal-footer">
                <p>${footer}</p>
                <button type="button" class="btn btn-warning" data-dismiss="modal">Ok</button>
            </div>
        
        </div> 
    </div>
</div>
            `;

        let modalContainer = document.createElement("div");
        modalContainer.id = modalContainerId;
        modalContainer.innerHTML = modalHtml;

        document.body.appendChild(modalContainer);

        let script = document.createElement("script");
        script.type = "text/javascript";
        script.text =
                `
                $('#${modalId}').modal("show");
                $('#${modalCloseButtonId}').click(function(){ $(${modalId}).modal("hide"); });
                 $('#${modalId}').on('hide.bs.modal', function(){
                       
                        let modalContainer = document.getElementById("${modalContainerId}");
                        if(modalContainer)
                        {
                            modalContainer.remove();
                        }
                    });
                `;
        modalContainer.appendChild(script);
    }
}