using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileWeb.Model
{
    public interface IFlightGearClient
    {
        //start the connecting to the FG (tcp)
        public void Start();
        //stop the connection
        public void Stop();
        //excute command in the FG
        public Task<Result> Execute(Command cmd);

    }
}
