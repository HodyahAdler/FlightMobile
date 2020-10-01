using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;

namespace FlightMobileWeb.Controllers
{
    [Route("/[controller]")]
    [ApiController]
    public class screenshotController : ControllerBase
    {
        private string HttpUrl;

        public screenshotController(IConfiguration configuration)
        {
            this.HttpUrl = configuration["HttpScreenshot"];
        }


        [HttpGet]
        public IActionResult Get()
        {
            HttpClient client = new HttpClient();

            try
            {
                //gt the img as streanm from http
                System.IO.Stream responseBody = client.GetStreamAsync(HttpUrl).Result;

                return Ok(responseBody);
            }
            catch (HttpRequestException e)
            {
                return NotFound(e);
            }
        }

    }
}