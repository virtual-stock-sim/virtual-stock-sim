import {Dependency, DependencyType, loadDependencies} from "./dependencyloader.js";

if(!document.getElementById("navbarInit"))
{
    // Create a hidden div to indicate that this script has already run
    let tag = document.createElement("div");
    tag.hidden = true;
    tag.id = "navbarInit"
    document.body.append(tag);

    let dependencies: Dependency[] =
        [
            {uri: "https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js", type: DependencyType.SCRIPT, async: false},
            {uri: "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js", type: DependencyType.SCRIPT, async: false},
            {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js", type: DependencyType.SCRIPT, async: false},
            {uri: "../../js_files/general.js", type: DependencyType.SCRIPT, async: false},
            {uri: "../../js_files/redirect.js", type: DependencyType.SCRIPT},
            {uri: "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css", type: DependencyType.STYLESHEET},
            {uri: "https://fonts.googleapis.com/icon?family=Material+Icons", type: DependencyType.STYLESHEET},
            {uri: "../../cssfiles/generalCSS/generalStyle.css", type: DependencyType.STYLESHEET},
            {uri: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css", type: DependencyType.STYLESHEET}
        ];
    loadDependencies(dependencies);
}