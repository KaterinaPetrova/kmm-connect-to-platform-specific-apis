import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject var viewModel = ViewModel()
    
    var body: some View {
        VStack(alignment: .center, spacing: 10.0) {
            Text(viewModel.text ?? "")
            if (viewModel.state == AppSocket.State.connected) {
                Button("Send message", action: { viewModel.sendMessage() })
            } else {
                Button("Connect!", action: { viewModel.connect() })
                    .disabled(viewModel.state != AppSocket.State.closed)
            }
            Button("Disconnect!", action: { viewModel.disconnect() })
                .disabled(viewModel.state == AppSocket.State.closed)
        }
    }
}

extension ContentView {
    
    class ViewModel: ObservableObject {
        
        let socket = AppSocket(url: "wss://echo.websocket.org")
        
        @Published var state: AppSocket.State = AppSocket.State.closed
        @Published var text: String? = nil
        
        init() {
            socket.messageListener = { [weak self] message in
                self?.text = "Echo " + message
            }
            socket.stateListener = { [weak self] state in
                self?.text = state.description() + (self?.socket.socketError?.description() ?? "")
                self?.state = state
            }
        }
        
        func sendMessage() {
            socket.send(msg: Greeting().greeting())
        }
        
        func connect() {
            socket.connect()
        }
        
        func disconnect() {
            socket.disconnect()
        }
    
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
