import io
import torch
from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import StreamingResponse
from PIL import Image
from diffusers import StableDiffusionControlNetPipeline, ControlNetModel, UniPCMultistepScheduler

app = FastAPI(title="Still Life Generator Engine")

print("Loading ControlNet & SD Pipelines...")
controlnet = ControlNetModel.from_pretrained(
    "lllyasviel/sd-controlnet-depth", torch_dtype=torch.float16
)
pipe = StableDiffusionControlNetPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5", 
    controlnet=controlnet, 
    torch_dtype=torch.float16
).to("cuda")

pipe.scheduler = UniPCMultistepScheduler.from_config(pipe.scheduler.config)

@app.post("/api/v1/generate-still-life")
async def generate_still_life(
    file: UploadFile = File(...),
    creepiness: float = Form(0.75),
    preset: str = Form("liminal_room")
):
    contents = await file.read()
    input_image = Image.open(io.BytesIO(contents)).convert("RGB").resize((512, 512))
    
    prompt = (
        "A horrifying Still Life creature, uncanny pale human copy, blank featureless face, "
        "stiff limbs, rubbery flesh texture, frozen in place, sitting on a chair, "
        f"located in a {preset}, raw flash photo, analogue VHS camera artifact, liminal space lore"
    )
    neg_prompt = "normal human, lively eyes, detailed iris, smiling, vibrant skin tone, cartoon, drawing, high quality render"

    output = pipe(
        prompt=prompt,
        negative_prompt=neg_prompt,
        image=input_image,
        num_inference_steps=25,
        guidance_scale=8.0,
        controlnet_conditioning_scale=creepiness
    ).images[0]

    img_byte_arr = io.BytesIO()
    output.save(img_byte_arr, format='JPEG')
    img_byte_arr.seek(0)
    
    return StreamingResponse(img_byte_arr, media_type="image/jpeg")
