using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace FlightMobileWeb.Model
{
    public class FlightGearClient : IFlightGearClient
    {
        private readonly BlockingCollection<AsyncCommand> queue;
        private readonly TcpClient client;
        private readonly Dictionary<string, string> simulatorPath;
        private volatile Boolean toStop;


        public FlightGearClient(IConfiguration configuration)
        {
            queue = new BlockingCollection<AsyncCommand>();
            simulatorPath = SetDictionary();

            client = new TcpClient(configuration["SimulatorIP"],
                int.Parse(configuration["SimulatorPort"]));
            this.Start();
        }

        public Dictionary<string, string> SetDictionary()
        {
            Dictionary<string, string> dictionary = new Dictionary<string, string>();
            dictionary.Add("Aileron", "/controls/flight/aileron");
            dictionary.Add("Rudder", "/controls/flight/rudder");
            dictionary.Add("Elevator", "/controls/flight/elevator");
            dictionary.Add("Throttle", "/controls/engines/current-engine/throttle");
            return dictionary;
        }

        public Task<Result> Execute(Command cmd)
        {
            var asyncCommand = new AsyncCommand(cmd);
            queue.Add(asyncCommand);
            return asyncCommand.Task;
        }

        private void sendValueToFG(NetworkStream ns, AsyncCommand command)
        {
            Result res = Result.Ok;
            byte[] bytesToSend;
            //doing the same for all the values that need to change in the FG
            foreach (KeyValuePair<string, string> kvp in simulatorPath)
            {
                //send set command
                Double propertyCommandValue = (Double)command.Command.GetType().GetProperty(
                    kvp.Key).GetValue(
                    command.Command, null);
                string sendLine = "set " + kvp.Value + " " + propertyCommandValue;
                bytesToSend = ASCIIEncoding.ASCII.GetBytes(sendLine + "\r\n");
                ns.Write(bytesToSend, 0, bytesToSend.Length);

                //send get command
                sendLine = "get " + kvp.Value;
                bytesToSend = ASCIIEncoding.ASCII.GetBytes(sendLine + "\r\n");
                ns.Write(bytesToSend, 0, bytesToSend.Length);

                //check that the simulator update
                byte[] bytes = new byte[1024];
                string acceptLine;
                try
                {
                    int bytesRead = ns.Read(bytes, 0, bytes.Length);
                    acceptLine = Encoding.ASCII.GetString(bytes, 0, bytesRead);
                }
                catch
                {
                    acceptLine = "ERR the simulator dont replies";
                }
                try
                {
                    Double simulatorValue = Double.Parse(acceptLine);
                    if (simulatorValue != propertyCommandValue)
                    {
                        res = Result.NotOk;
                        break;
                    }
                }
                catch
                {
                    res = Result.NotOk;
                    break;
                }
            }
            //finish the task
            command.Completion.SetResult(res);
        }

        private void RunConnectToFG()
        {
            NetworkStream ns = client.GetStream();

            //send data command
            byte[] bytesToSend = ASCIIEncoding.ASCII.GetBytes("data\r\n");
            ns.Write(bytesToSend, 0, bytesToSend.Length);


            //timeout for gettind a answer from the simulator
            client.ReceiveTimeout = 10000;

            while (toStop == false)
            {
                //run al the command in the queue
                foreach (AsyncCommand command in queue.GetConsumingEnumerable())
                {
                    sendValueToFG(ns, command);
                }
            }
        }
        public void Start()
        {
            Task.Factory.StartNew(delegate ()
            {
                RunConnectToFG();
                client.Close();

            });
        }

        public void Stop()
        {
            this.toStop = true;
        }

    }
}
