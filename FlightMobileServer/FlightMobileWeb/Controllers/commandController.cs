using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using FlightMobileWeb.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace FlightMobileWeb.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class commandController : ControllerBase
    {
        private IFlightGearClient telnetClient;

        public commandController(IFlightGearClient flightGearClient)
        {
            this.telnetClient = flightGearClient;
        }

        [HttpPost]
        public IActionResult Post([FromBody]Command command)
        {
            //check if the json correct
            if (Double.IsNaN(command.Aileron) ||
                Double.IsNaN(command.Elevator) ||
                Double.IsNaN(command.Rudder) ||
                Double.IsNaN(command.Throttle))
            {
                return NotFound();
            }

            //excute the connand - tochange the FG
            Task<Result> commandTask = telnetClient.Execute(command);
            //wait to the command o end
            commandTask.Wait();
            //check and send if the command succesed
            Result result = commandTask.Result;

            if (result == Result.Ok)
            {
                return Ok();
            }
            else
            {
                return NotFound();
            }

        }
    }
}