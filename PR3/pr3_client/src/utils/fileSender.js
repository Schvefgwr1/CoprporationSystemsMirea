export const sendFileForAnalysis = async (socket, file) => {
    const reader = new FileReader();

    return new Promise((resolve, reject) => {
        if (file.type !== "text/plain") {
            reject(new Error("Only plain text (.txt) files are supported."));
            return;
        }

        reader.onload = () => {
            try {
                const arrayBuffer = reader.result;
                const uint8Array = new Uint8Array(arrayBuffer);
                const binaryString = Array.from(uint8Array)
                    .map((byte) => String.fromCharCode(byte))
                    .join("");
                const base64Content = btoa(binaryString);

                const message = {
                    fileName: file.name,
                    content: base64Content,
                };

                const handleMessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        if (data.error) {
                            reject(new Error(data.message));
                        } else {
                            resolve(data);
                        }
                    } catch (e) {
                        reject(new Error("Invalid response from server"));
                    } finally {
                        socket.removeEventListener("message", handleMessage);
                    }
                };

                socket.addEventListener("message", handleMessage);
                socket.send(JSON.stringify(message));
            } catch (err) {
                reject(new Error("Failed to encode file to base64"));
            }
        };

        reader.onerror = () => {
            reject(new Error("Error reading file."));
        };

        reader.readAsArrayBuffer(file); // снова читаем бинарно
    });
};
