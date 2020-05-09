import { DependencyType, loadDependencies } from "./dependencyloader.js";
const containerId = "loading-wheel-container";
const wheelId = "loading-wheel";
const msgId = "loading-wheel-message";
export function displayLoadingWheel(message) {
    // Check if the element already exists
    if (!document.getElementById(wheelId)) {
        loadDependencies([{ uri: "../cssfiles/loadingwheel.css", type: DependencyType.STYLESHEET }]);
        let container = document.createElement("div");
        container.id = containerId;
        container.className += " loading-wheel-container";
        let wheel = document.createElement("span");
        wheel.id = wheelId;
        wheel.className += " loading-wheel";
        let msg = document.createElement("h4");
        msg.id = msgId;
        msg.className += " loading-wheel-message";
        msg.innerText = message;
        container.appendChild(wheel);
        container.appendChild(msg);
        document.body.appendChild(container);
    }
}
export function removeLoadingWheel() {
    let wheel = document.getElementById(containerId);
    if (wheel)
        document.body.removeChild(wheel);
}
