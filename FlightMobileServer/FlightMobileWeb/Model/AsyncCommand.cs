using FlightMobileWeb.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileWeb
{
    public enum Result { Ok, NotOk }

    public class AsyncCommand
    {
        //the new values the command need to change
        public Command Command { get; private set; }
        //the task that make the values change in the FG
        public Task<Result> Task { get => Completion.Task; }

        public TaskCompletionSource<Result> Completion { get; private set; }
        public AsyncCommand(Command command)
        {
            Command = command;
            Completion = new TaskCompletionSource<Result>(
            TaskCreationOptions.RunContinuationsAsynchronously);
        }
    }
}
