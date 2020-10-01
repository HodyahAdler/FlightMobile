using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace FlightMobileWeb.Model
{
    public class Command
    {
        public Command()
        {
            Aileron = Double.NaN;
            Rudder = Double.NaN;
            Elevator = Double.NaN;
            Throttle = Double.NaN;

        }

        [JsonPropertyName("aileron")]
        public Double Aileron { get; set; }

        [JsonPropertyName("rudder")]
        public Double Rudder { get; set; }

        [JsonPropertyName("elevator")]
        public Double Elevator { get; set; }

        [JsonPropertyName("throttle")]
        public Double Throttle { get; set; }
    }
}
