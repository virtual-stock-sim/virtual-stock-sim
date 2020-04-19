export function deserialize(json) {
    return Object.assign({}, JSON.parse(json));
}
export function serialize(obj) {
    return JSON.stringify(obj);
}
